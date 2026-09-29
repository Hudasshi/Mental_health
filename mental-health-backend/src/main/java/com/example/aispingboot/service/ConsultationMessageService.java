package com.example.aispingboot.service;

import com.example.aispingboot.entity.ConsultationMessage;
import com.example.aispingboot.mapper.ConsultationMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/29 13:39
 * @description
 */
@Service
public class ConsultationMessageService {
    @Autowired
    private ConsultationMessageMapper consultationMessageMapper;
    public ConsultationMessage saveUserMessage(Long sessionId, String content,String emotionTag){
        ConsultationMessage userMessage = ConsultationMessage.builder()
                .sessionId(sessionId)
                .senderType(1)
                .messageType(1)
                .content(content)
                .emotionTag(emotionTag)
                .createdAt(LocalDateTime.now())
                .build();
        consultationMessageMapper.insert(userMessage);
        return  userMessage;
    }
}
