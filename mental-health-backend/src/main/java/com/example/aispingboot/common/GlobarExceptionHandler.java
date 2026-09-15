package com.example.aispingboot.common;

import com.example.aispingboot.exception.BusinessException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/9 15:21
 * @description 异常处理
 */
@RestControllerAdvice
public class GlobarExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleException(MethodArgumentNotValidException e){
        String Message=e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage).collect(Collectors.joining(","));
//        1. `e.getBindingResult()`：拿到校验结果对象
//        2. `.getFieldErrors()`：获取**所有校验失败的字段错误集合**（一个 DTO 可能多个字段同时不合法）
//        3. `.stream()`：转成 Java 流式操作
//        4. `.map(FieldError::getDefaultMessage)`：取出每个错误里写的 `message` 提示文本，比如`用户名不能为空`、`密码长度不能小于6位`
//        5. `.collect(Collectors.joining(", "))`：把多条错误信息，用 `, ` 拼接成**一条字符串**
//      > 举例：同时用户名空 + 密码太短 → 拼接结果：`用户名不能为空, 密码长度不能小于6位`
        return Result.ERROR(ResultCode.PARAM_ERROR.getCode(),ResultCode.PARAM_ERROR.getMsg(), Message);
    }
    /**
     * 捕获业务异常 BusinessException
     * Service 层抛出的业务异常统一在这里处理
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        // 如果异常携带额外的数据
        if (e.getData() != null) {
            return Result.ERROR(e.getCode(), e.getMessage(), e.getData());
        }
        return Result.ERROR(e.getCode(), e.getMessage(), null);
    }
}
