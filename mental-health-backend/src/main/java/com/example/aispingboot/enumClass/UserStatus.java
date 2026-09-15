package com.example.aispingboot.enumClass;

import lombok.Getter;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/14 15:04
 * @description
 */
@Getter
public enum UserStatus {
    DISABLED(0,"禁用"),
    NORMAL(1,"正常");
    private final Integer code;
    private final String description;

    UserStatus(Integer code, String description){
        this.code=code;
        this.description=description;
    }
    public static UserStatus fromCode(Integer code) {
        for (UserStatus status : UserStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的用户状态代码: " + code);
    }
}
