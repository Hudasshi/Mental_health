package com.example.aispingboot.common;

import lombok.Data;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/7 19:56
 * @description 统一返回封装类（成功 / 失败的通用 JSON 结构）
 */
@Data
public class Result<T> {
    private String code;
    private String msg;
    private T data;
    public static <T> Result<T> OK(){
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(ResultCode.SUCCESS.getMsg());
        return result;
    }
    public static <T> Result<T> OK(T data){
        Result<T> result =OK();
        result.setData(data);
        return result;
    }
    public static <T> Result<T> ERROR(){
        Result<T> result = new Result<>();
        result.setCode(ResultCode.ERROR.getCode());
        result.setMsg(ResultCode.ERROR.getMsg());
        return result;
    }
    public static <T> Result<T> ERROR(String Code,String msg,T data) {
        Result<T> result = ERROR();
        result.setCode(Code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
}
