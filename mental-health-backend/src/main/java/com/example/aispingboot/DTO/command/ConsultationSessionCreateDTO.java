package com.example.aispingboot.DTO.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/28 17:31
 * @description 前端调用 `POST /session/start` 时传过来的 JSON：会话标题（可选）+ 开场消息（必填）。用一个 DTO 承接，并在字段上加 Jakarta Validation 注解做**入参校验**
 */
@Data
public class ConsultationSessionCreateDTO {
    @Size(max=200,message = "会话标题最多200个字符")
    private String sessionTitle;
    @NotBlank(message = "初始消息不能为空")
    @Size(max=2000,message = "初始消息最多2000个字符")
    private String initialMessage;
}
