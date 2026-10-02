package com.example.aispingboot.AiService;

import com.example.aispingboot.DTO.command.ConsultationSessionCreateDTO;
import com.example.aispingboot.DTO.response.ConsultationMessageResponseDTO;
import com.example.aispingboot.entity.ConsultationSession;
import com.example.aispingboot.service.ConsultationMessageService;
import com.example.aispingboot.service.ConsultationSessionService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/28 21:45
 * @description
 */
/**
 * AI 心理支持 Service（早期骨架，视频里先放在 AiService 包下）
 * <p>
 * 注意：这一步只是为了灭掉 Controller 里的红线。
 * 真正落库的业务后来改到了 service 包下的 ConsultationSessionService。
 */
@Service // 标记为 Spring Bean，Controller 才能 @Autowired 进来
public class PsychologicalSupportService {
    @Autowired
    private ConsultationSessionService consultationSessionService;
    @Autowired
    private ConsultationMessageService consultationMessageService;

    @Autowired
    @Qualifier("open-ai")
    private ChatClient chatClient;
    @Autowired
    private ChatMemory chatMemory;   // 从 ChatClientConfig 里 @Bean 进来的
    /**
     * 启动一次心理聊天会话
     *
     * @param userId    当前登录用户 ID（从 JWT 解出来）
     * @param createDTO 前端传过来的会话标题 + 初始消息
     * @return 会话的结构化信息（此时还没真返回，先 null 占位）
     */
    public StructOutPut.StreamChatSession startSession(Long userId, ConsultationSessionCreateDTO createDTO) {
        //创建数据库会话记录
        ConsultationSession consultationSession = consultationSessionService.createSession(userId, createDTO);

        // 【步骤 3】把初始消息写进数据库
        consultationMessageService.saveUserMessage(consultationSession.getId(),createDTO.getInitialMessage(),null);

        String sessionId = "sessiondId_"+consultationSession.getId();
        // 3.2 当前时间戳（毫秒）
        long now = System.currentTimeMillis();

        // 3.3 过期时间：当前时间 + 24 小时
        //     86400000L = 24 * 60 * 60 * 1000 = 24 小时的毫秒数
        //     注意必须写 L 后缀，养成习惯避免溢出
        long expiryTime = now + 86400000L; // 24 小时

        return new StructOutPut.StreamChatSession(sessionId,userId,createDTO.getInitialMessage(),now,expiryTime,1,"ACTIVE");
    }
    /**
     * 流式心理对话
     * @param sessionId   业务会话 ID（"session_123"）
     * @param userMessage 用户本轮发的话
     * @return Flux<String>，每个元素是一个 token
     */
    public Flux<String> streamPsychologicalChat(String sessionId, String userMessage) {
        return Flux.create( sink -> {

            Long dbSessionId = extractSessionId(sessionId);
            if (dbSessionId == null) {
                sink.error(new RuntimeException("会话ID格式错误"));
                return;
            }

            // ========== 初始消息去重逻辑 ==========
            boolean isInitialMessage = false;

            // 1. 查这个会话现在一共有几条消息
            Integer messageCount =
                    consultationMessageService.getMessageCountBySessionId(dbSessionId);

            // 2. 如果只有 1 条，说明那就是 /session/start 存的开场消息
            if (messageCount == 1) {
                // 查最后一条（也就是那唯一一条）消息
                ConsultationMessageResponseDTO lastMessage =
                        consultationMessageService.getLastMessageBySessionId(dbSessionId);

                // 3. 判断：这条消息是不是用户发的（senderType==1），
                //    而且内容和当前前端传的 userMessage 一模一样
                if (lastMessage != null
                        && lastMessage.getSenderType() == 1
                        && userMessage.equals(lastMessage.getContent())) {
                    // 命中：说明前端把"开场消息"又发到 /stream 了，不要重复存
                    isInitialMessage = true;
                }
            }

            // 4. 如果不是初始消息，才把用户这次的话存进数据库
            if (!isInitialMessage) {
                consultationMessageService.saveUserMessage(dbSessionId, userMessage, null);
            }

            // 【后续】这里就要调 SpringAI 的 ChatClient，把 userMessage 发给大模型，
            //         每个 token 出来就 sink.next(token)，结束 sink.complete()

            // 进行流式对话
            //构建系统提示词
            List<Message> userMessages= new ArrayList<>();
            userMessages.add(new UserMessage(userMessage));

            // ChatMemory 按 conversationId 分桶，不同会话的历史互不干扰
            // 用 "conversation_" + 前端传来的 sessionId（"session_12"）作为记忆桶 ID
            String conversationId = "conversation_" + sessionId;
            // 手动把用户消息塞进 ChatMemory（按 conversationId 分桶）
            chatMemory.add(conversationId, userMessages);

            //用于存储完整ai响应
            StringBuilder fullResponse = new StringBuilder();


            Prompt prompt = new Prompt(List.of(
                    new SystemMessage(PromptManage.PSYCHOLOGICAL_SUPPORT_SYSTEM_PROMPT)
            ));
            // 使用chatClient发送消息到Open AI
            chatClient.prompt(prompt)
                    .user(userMessage)
                    //**`.advisors(...)`**：SpringAI 里的**增强器（Advisor）**自动把这个会话的历史聊天记录拼到 prompt 里面
                    .advisors(advisorSpec ->advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                    .stream()
                    .content()
                    .doOnNext(fragment -> {
                        //将每段流式对话进行拼接
                        fullResponse.append(fragment);
                        sink.next(fragment);
                    })
                    .doOnComplete(() -> {
                        String completeRes = fullResponse.toString();

                        // 1. 将AI返回的完整内容保存到数据库（senderType=2）
                        consultationMessageService.saveAiMessage(dbSessionId, completeRes, "openai");

                        // 2. 把 AI 回复也加进 ChatMemory，下一轮对话才能记住
                        List<Message> aiMessages = new ArrayList<>();
                        aiMessages.add(new AssistantMessage(completeRes));
                        chatMemory.add(conversationId, aiMessages);

                        // 3. 告诉前端：流结束了
                        sink.complete();
                    })
                    .doOnError(error -> {
                        // 出错时把错误推给前端
                        sink.error(error);
                    })
                    .subscribe(); // 订阅并启动流

        });
    }
    /**
     * 把前端传的 "session_123" 还原成数据库主键 123L
     * - 格式不对（null / 不是以 session_ 开头）返回 null
     */
    public Long extractSessionId(String sessionId){
        //sessionId不为空，并且sessionId.startsWith前面一定要包含“ ”
        if(sessionId!=null&&sessionId.startsWith("session_")){
            //`substring("session_".length())`：从 `"session_".length()`（=8）开始截，正好拿到 `"123"`；
            String idStr = sessionId.substring("session_".length());
            return Long.parseLong(idStr);
        }
        return null;
    }
}