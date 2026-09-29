package com.example.aispingboot.AiService;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/28 17:28
 * @description AI 对话模块的**结构化输出容器类**
 */
public class StructOutPut {
    public record StreamChatSession(
      String sessionId,
      Long userHash,
      String initialMessage,
      Long startTime,
      Long expiryTime,
      Integer messageCount,
      String status
    ){}
}
