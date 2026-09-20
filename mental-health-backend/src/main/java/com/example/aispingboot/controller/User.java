package com.example.aispingboot.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.aispingboot.DTO.command.UserLoginCommandDTO;
import com.example.aispingboot.DTO.command.UserRegisterCommandDTO;
import com.example.aispingboot.DTO.response.UserLoginResponseDTO;
import com.example.aispingboot.common.Result;
import com.example.aispingboot.service.UserService;
import com.example.aispingboot.util.JwtTokenUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/9 11:20
 * @description
 */
@RestController
@RequestMapping("/api/user")
public class User {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public Result<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginCommandDTO commandDTO){
        UserLoginResponseDTO result = userService.login(commandDTO);
        return Result.OK(result);
    }
    @PostMapping("/add")
    public Result<UserLoginResponseDTO.UserDetailResponseDTO> register(@Valid @RequestBody UserRegisterCommandDTO commandDTO){
        UserLoginResponseDTO.UserDetailResponseDTO result = userService.register(commandDTO);
        return Result.OK(result);
    }
    //requestUser：1.创建接口获取当前登录用户
//获取用户数据
    @GetMapping("/current")
    public Result<UserLoginResponseDTO.UserDetailResponseDTO> getCurrentUser(){
        //步骤20：获取当前请求携带的token
        String token = JwtTokenUtil.getCurrentToken();
        //校验token签名、有效期，解析得到DecodedJWT对象
        DecodedJWT jwt = JwtTokenUtil.verifyToken(token);
        //从jwt的载荷中取出存入的userId
        Long userId = jwt.getClaim("userId").asLong();
        //根据userId查询数据库，组装用户详情DTO
        UserLoginResponseDTO.UserDetailResponseDTO result = userService.getUserById(userId);
        //封装统一返回结果，返回给前端
        return Result.OK(result);
    }

}
