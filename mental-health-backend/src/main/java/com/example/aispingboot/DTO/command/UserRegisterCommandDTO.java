package com.example.aispingboot.DTO.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/16 15:00
 * @description
 */
@Data
public class UserRegisterCommandDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3到50个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在6到50个字符之间")
    private String password;
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    @NotBlank(message = "邮箱不能为空")
    private String email;
    @Pattern(regexp = "^1[3-9]\\d{9}$",message = "手机号格式错误")
    private String phone;
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
    private LocalDate birthday;
    private Integer gender;
    private Integer userType=1;
}
