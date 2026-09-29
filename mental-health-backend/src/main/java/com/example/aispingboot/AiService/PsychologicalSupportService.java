package com.example.aispingboot.AiService;

import com.example.aispingboot.DTO.command.ConsultationSessionCreateDTO;
import com.example.aispingboot.entity.ConsultationSession;
import com.example.aispingboot.service.ConsultationMessageService;
import com.example.aispingboot.service.ConsultationSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}