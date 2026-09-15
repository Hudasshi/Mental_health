package com.example.aispingboot.DTO.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/9 17:28
 * @description
 */
@Builder
@Data
public class UserLoginResponseDTO {
    // 登录凭证 token
    private String token;
    // 角色类型
    private String roleType;
    // 用户详细信息
    private UserDetailResponseDTO userInfo;

    /**
     * 用户详细信息（内部静态类）
     * 只返回前端需要展示的字段，不含密码等敏感信息
     */
    @Builder
    @Data
    public static class UserDetailResponseDTO {
        private Long id;
        private String username;
        private String email;
        private String nickname;
        private String avatar;
        private String phone;
        private Integer gender;
        // 性别展示名（如"男"/"女"）
        private String genderDisplayName;
        private LocalDate birthday;
        private Integer userType;
        // 用户类型展示名（如"普通用户"/"管理员"）
        private String userTypeDisplayName;
        private Integer status;
        // 状态展示名（如"正常"/"禁用"）
        private String statusDisplayName;
        // 展示名称（昵称优先，没有则用用户名）
        private String displayName;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}