package com.example.aispingboot.controller;

import com.example.aispingboot.DTO.command.UserLoginCommandDTO;
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
}
