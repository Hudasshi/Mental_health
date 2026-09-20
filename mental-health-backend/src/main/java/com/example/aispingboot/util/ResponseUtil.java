package com.example.aispingboot.util;


import cn.hutool.json.JSONUtil;
import com.example.aispingboot.common.Result;
import com.example.aispingboot.common.ResultCode;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/19 12:42
 * @description 过滤器**不经过 Controller / 全局异常处理器**，所以在过滤器里发现"没有 token / token 无效"时，必须自己往 `HttpServletResponse` 里写错误响应，
 */
//requestUser：11 写入响应
public class ResponseUtil {
    public static void wirteError(HttpServletResponse response, ResultCode resultCode){
        int status = switch (resultCode){
            //401
            case UNAUTHORIZED,ACCESS_UNAUTHORIZED,TOKEN_INVALID, TOKEN_EXPIRED, TOKEN_BLOCKED->
                    HttpStatus.UNAUTHORIZED.value();
            //403
            case  TOKEN_ACCESS_FORBIDDEN->
                    HttpStatus.FORBIDDEN.value();
            default -> HttpStatus.BAD_REQUEST.value();
        };
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try(PrintWriter writer = response.getWriter()){
           String jsonResponse = JSONUtil.toJsonStr(Result.ERROR(resultCode.getCode(),resultCode.getMsg(),null));
           writer.print(jsonResponse);
           writer.flush();
        }catch (IOException e){
            System.out.println("写入响应失败"+e.getMessage());
        }
    }
}
