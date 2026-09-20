package com.example.aispingboot.controller;

import com.example.aispingboot.DTO.command.UserLoginCommandDTO;
import com.example.aispingboot.DTO.command.UserRegisterCommandDTO;
import com.example.aispingboot.DTO.response.UserLoginResponseDTO;
import com.example.aispingboot.common.Result;
import com.example.aispingboot.service.UserService;
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
        return null;
    }
}
