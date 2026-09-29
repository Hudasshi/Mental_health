package com.example.aispingboot.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.aispingboot.AiService.PsychologicalSupportService;
import com.example.aispingboot.AiService.StructOutPut;
import com.example.aispingboot.DTO.command.ConsultationSessionCreateDTO;
import com.example.aispingboot.common.Result;
import com.example.aispingboot.util.JwtTokenUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/28 17:18
 * @description ai对话控制器
 */
@RestController
@RequestMapping("/api/psychological-chat")
public class PsychologicalChat {

    // 【① 注入 service】
    @Autowired
    private PsychologicalSupportService psychologicalSupportService;

    @PostMapping("/session/start")
    public Result<StructOutPut.StreamChatSession> startSession(
            @Valid @RequestBody ConsultationSessionCreateDTO createDTO) {

        // 获取当前登录用户
        String token = JwtTokenUtil.getCurrentToken();
        DecodedJWT jwt = JwtTokenUtil.verifyToken(token);
        Long userId = jwt.getClaim("userId").asLong();

        // 【① 调用 service】
        StructOutPut.StreamChatSession session = psychologicalSupportService.startSession(userId, createDTO);

        // 还没改，等 service 写完再把这里换成
        return Result.OK(session);

    }
}
