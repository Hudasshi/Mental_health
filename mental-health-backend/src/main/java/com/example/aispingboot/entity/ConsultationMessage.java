package com.example.aispingboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/29 13:40
 * @description
 */
@Data
@TableName("consultation_message")
@Builder
public class ConsultationMessage {

    /** 消息主键 ID（自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属会话 ID
     * - 外键，指向 consultation_session.id
     * - 一条消息必须挂在一个会话下，所以非空
     */
    @NotNull(message = "会话ID不能为空")
    @TableField("session_id")
    private Long sessionId;

    /**
     * 发送者类型：1=用户，2=AI助手
     * - 用数字而不是字符串，省空间 + 防脏数据
     * - 需要中文时调 getSenderTypeDesc()
     */
    private Integer senderType;

    /** 消息类型：1=文本（后续可扩展 2=语音、3=图片等） */
    private Integer messageType;

    /** 消息正文内容，必填 */
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /** 情绪标签（如 "焦虑"/"平静"/"悲伤"），最长 50 */
    @Size(max = 50, message = "情绪标签长度不能超过50个字符")
    @TableField("emotion_tag")
    private String emotionTag;

    /** 这条消息用的哪个 AI 模型（如 "doubao-pro"），最长 50 */
    @Size(max = 50, message = "AI模型名称长度不能超过50个字符")
    @TableField("ai_model")
    private String aiModel;

    /** 消息创建时间 */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 把 senderType 数字翻译成中文（前端展示用）
     */
    public String getSenderTypeDesc() {
        if (senderType == null) return "未知";
        return switch (senderType) {
            case 1 -> "用户";
            case 2 -> "AI助手";
            default -> "未知";
        };
    }

    /**
     * 把 messageType 数字翻译成中文
     */
    public String getMessageTypeDesc() {
        if (messageType == null) return "未知";
        return switch (messageType) {
            case 1 -> "文本";
            default -> "未知";
        };
    }
}
