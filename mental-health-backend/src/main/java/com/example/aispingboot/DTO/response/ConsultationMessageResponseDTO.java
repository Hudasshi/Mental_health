package com.example.aispingboot.DTO.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/29 23:09
 * @description
 */
@Data
public class ConsultationMessageResponseDTO {
    private Long id;
    private Long sessionId;
    private Integer senderType;        // 1:用户 2:AI助手
    private String senderTypeDesc;     // "用户"/"AI助手"
    private Integer messageType;       // 1:文本
    private String messageTypeDesc;    // "文本"
    private String content;
    private String emotionTag;
    private String aiModel;
    private LocalDateTime createdAt;
    private Integer contentLength;     // 消息长度（字符数）

    /** 计算消息长度 */
    public void calculateContentLength() {
        this.contentLength = content != null ? content.length() : 0;
    }
}
