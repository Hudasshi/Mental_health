package com.example.aispingboot.exception;

import lombok.Getter;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/9 18:29
 * @description
 */
@Getter
public class BusinessException  extends  RuntimeException{
    private final String code;
    private final String message;
    private final Object data;
    public BusinessException(String message) {
        super(message);
        this.code = "BUSINESS_ERROR";
        this.message = message;
        this.data = null;
    }
}
