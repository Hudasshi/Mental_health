package com.example.aispingboot.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author HU
 * @version 1.0
 * @date 2026/10/1 20:38
 * @description
 */
/**
 * SpringAI ChatClient 配置类
 * 负责：
 *   1. 造一个 ChatMemory（对话记忆）Bean
 *   2. 造一个名叫 "open-ai" 的 ChatClient Bean，挂上记忆 + 默认系统提示词
 */
@Configuration
public class ChatClientConfig {

    /**
     * 对话记忆 Bean
     * MessageWindowChatMemory：窗口记忆，只保留最近 N 条消息
     * 为什么用窗口记忆？
     *   - 大模型上下文有长度限制，不能把所有历史都塞进去；
     *   - 保留最近 30 条（15 轮对话）足够连续聊天，又不会超长。
     */
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(30)   // 保留最新30条消息
                .build();
    }

    /**
     * 造一个名叫 "open-ai" 的 ChatClient
     * @param openAiChatModel SpringAI 自动配置注入的底层模型（已经读了 yml 里的 api-key/base-url）
     */
    @Bean("open-ai")
    public ChatClient openAiChatClient(OpenAiChatModel openAiChatModel) {
        return ChatClient.builder(openAiChatModel)
                // 挂上记忆 advisor：以后每个请求 SpringAI 自动从 chatMemory 取历史拼进去
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory()).build()
                )
                // 默认系统提示词：每次对话自动带上，不用每个请求重复写
                .defaultSystem("你是一个专业的心理疏导师，温和耐心，善于倾听，能够提供专业的心理支持和建议")
                .build();
    }
}
