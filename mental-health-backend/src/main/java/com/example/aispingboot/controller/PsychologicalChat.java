package com.example.aispingboot.controller;

import cn.hutool.json.JSONUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.aispingboot.AiService.PsychologicalSupportService;
import com.example.aispingboot.AiService.StructOutPut;
import com.example.aispingboot.DTO.command.ConsultationSessionCreateDTO;
import com.example.aispingboot.DTO.command.ConsultationStreamDTO;
import com.example.aispingboot.common.Result;
import com.example.aispingboot.common.ResultCode;
import com.example.aispingboot.util.JwtTokenUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;

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
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(@Valid @RequestBody ConsultationStreamDTO streamDTO) {
        // 【1】和 /session/start 一样，先取当前登录用户
        String token = JwtTokenUtil.getCurrentToken();
        DecodedJWT jwt = JwtTokenUtil.verifyToken(token);
        Long userId = jwt.getClaim("userId").asLong();

        // 【2】如果 userId 拿不到（token 无效/过期），不能直接 throw
        //     因为这是 SSE 流，错误必须"作为一帧事件"推给前端
        if (userId == null) {
            return Flux.just(
                    ServerSentEvent.<String>builder()
                            .event("error")   // 事件名叫 error，前端监听 error 事件
                            .data(JSONUtil.toJsonStr(
                                    Result.ERROR(ResultCode.UNAUTHORIZED.getCode(),
                                            ResultCode.UNAUTHORIZED.getMsg(),"用户未登录")
                            ))
                            .build()
            );
        }

        // 【3】后续调编排层 service，现在先 return null
        // TODO: return psychologicalSupportService.streamPsychologicalChat(...)
        return psychologicalSupportService.streamPsychologicalChat(streamDTO.getSessionId(),streamDTO.getUserMessage())
                .map(Fragment -> {
                    return ServerSentEvent.<String>builder()
                            .event("message")
                            .data(JSONUtil.toJsonStr(Result.OK(Map.of("content",Fragment,"type","normal"))))
                            .build();
                })
                // 【4】在流末尾再追加一帧 "done"，告诉前端：回答完了
                //     concatWith：把后面这个 Flux 拼在主流后面，主流完了就发它
                .concatWith(Flux.just(
                        ServerSentEvent.<String>builder()
                                .event("done")
                                .data("{}")
                                .build()
                ))
                // 【5】每帧之间加 50ms 延迟，确保"打字机"效果
                //     不加的话 AI 吐字太快，前端看起来像一次性返回，不像流式
                .delayElements(Duration.ofMillis(50));
    }
}
