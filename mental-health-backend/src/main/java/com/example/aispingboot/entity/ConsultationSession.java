package com.example.aispingboot.entity;

// === MyBatis-Plus 注解 ===
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
@Data
@TableName("consultation_session") // 对应数据库表：consultation_session
@Builder
public class ConsultationSession {

    /**
     * 会话主键 ID（数据库自增）
     * - @TableId 标记这是主键
     * - type = IdType.AUTO 表示交给数据库 AUTO_INCREMENT 自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发起会话的用户 ID
     * - @TableField("user_id") 指定对应数据库列名 user_id
     * - 因为 Java 驼峰 userId 和数据库下划线 user_id 不一致，必须显式指定
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 会话标题（前端可不传）
     * - @Size(max = 200) 复用 Jakarta Validation，和入参 DTO 保持一致的长度上限
     * - @TableField("session_title") 对应数据库列 session_title
     */
    @Size(max = 200, message = "会话标题长度不能超过200个字符")
    @TableField("session_title")
    private String sessionTitle;

    /**
     * 会话开始时间
     * - 用 LocalDateTime（Java 8+ 时间 API），不用 Date
     * - 对应数据库列 started_at
     */
    @TableField("started_at")
    private LocalDateTime startedAt;

    /**
     * 最后一次情绪分析结果（JSON 字符串）
     * - 字段先留着，本步不写值；后续 AI 情绪分析模块会回填
     */
    @TableField("last_emotion_analysis")
    private String lastEmotionAnalysis;

    /**
     * 最后一次情绪分析的更新时间
     */
    @TableField("last_emotion_analysis_updated_at")
    private LocalDateTime lastEmotionAnalysisUpdatedAt;
}