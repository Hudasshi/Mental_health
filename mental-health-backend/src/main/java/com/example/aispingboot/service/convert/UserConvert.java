package com.example.aispingboot.service.convert;

import com.example.aispingboot.DTO.response.UserLoginResponseDTO;
import com.example.aispingboot.entity.UserEntity;

/**
 * 用户实体结果转换类
 */
public class UserConvert {

    /**
     * User实体转换为详情响应DTO
     * @param user User实体
     * @return 用户详情响应DTO
     */
    public static UserLoginResponseDTO.UserDetailResponseDTO entityToDetailResponse(UserEntity user) {
        return UserLoginResponseDTO.UserDetailResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .phone(user.getPhone())
                .gender(user.getGender())
                .genderDisplayName(getGenderDisplayName(user.getGender()))

                .birthday(user.getBirthday())
                .userType(user.getUserType())
                .userTypeDisplayName(user.getUserTypeDisplayName())
                .status(user.getStatus())
                .statusDisplayName(user.getStatusDisplayName())
                .displayName(user.getDisplayName())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * 构建登录响应DTO
     * @param token JWT令牌
     * @param userInfo 用户信息
     * @return 登录响应DTO
     */
    public static UserLoginResponseDTO entityToLoginResponse(String token, UserLoginResponseDTO.UserDetailResponseDTO userInfo) {
        return UserLoginResponseDTO.builder()
                .userInfo(userInfo)
                .token(token)
                .roleType(userInfo.getUserType().toString())
                .build();
    }

    /**
     * 获取性别显示名称
     * @param gender 性别代码
     * @return 性别显示名称
     */
    private static String getGenderDisplayName(Integer gender) {
        if (gender == null) {
            return "未知";
        }
        switch (gender) {
            case 1:
                return "男";
            case 2:
                return "女";
            default:
                return "未知";
        }
    }
}