package com.example.aispingboot.DTO.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/9 11:29
 * @description 加上校验注解
 */
@Data
public class UserLoginCommandDTO {
    @NotBlank(message = "用户名或邮箱不为空")
    @Size(max=20,message = "用户名或邮箱最长不超过20个字符")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Size(min=6,max=20,message = "密码c长度必须再6到20之间")
    private String password;

}
