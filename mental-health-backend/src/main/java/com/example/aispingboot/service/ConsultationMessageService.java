package com.example.aispingboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.aispingboot.DTO.response.ConsultationMessageResponseDTO;
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
    /**
     * 查某个会话下一共有多少条消息
     * 用途：判断是不是"初始消息"（如果总共只有 1 条，说明那就是开场消息）
     */
    public Integer getMessageCountBySessionId(Long sessionId) {
        // LambdaQueryWrapper：MyBatis-Plus 的链式条件构造器
        // 用 Lambda 方法引用 ConsultationMessage::getSessionId，编译期就能检查字段名
        LambdaQueryWrapper<ConsultationMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConsultationMessage::getSessionId, sessionId);

        // selectCount 返回 Long，转成 Integer 返回
        Long count = consultationMessageMapper.selectCount(queryWrapper);
        return count.intValue();
    }
    //获取会话最后一条消息
    public ConsultationMessageResponseDTO getLastMessageBySessionId(Long sessionId){
        LambdaQueryWrapper<ConsultationMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConsultationMessage::getSessionId,sessionId)
                .orderByDesc(ConsultationMessage::getCreatedAt)
                .last("limit 1");
        ConsultationMessage lasrMessage = consultationMessageMapper.selectOne(queryWrapper);
        return lasrMessage !=null ? convertToResponseDTO(lasrMessage):null;

    }
    private ConsultationMessageResponseDTO convertToResponseDTO(ConsultationMessage message) {
        if (message == null) {
            return null;
        }
        // 手动逐字段赋值，确保转换的准确性和可控性
        ConsultationMessageResponseDTO responseDTO = new ConsultationMessageResponseDTO();
        responseDTO.setId(message.getId());
        responseDTO.setSessionId(message.getSessionId());
        responseDTO.setSenderType(message.getSenderType());
        responseDTO.setMessageType(message.getMessageType());
        responseDTO.setContent(message.getContent());
        responseDTO.setEmotionTag(message.getEmotionTag());
        responseDTO.setAiModel(message.getAiModel());
        responseDTO.setCreatedAt(message.getCreatedAt());

        // 设置描述字段（通过实体自带的翻译方法）
        responseDTO.setSenderTypeDesc(message.getSenderTypeDesc());
        responseDTO.setMessageTypeDesc(message.getMessageTypeDesc());

        // 计算消息长度
        responseDTO.calculateContentLength();

        return responseDTO;
    }
    /**
     * 保存一条 AI 回复消息
     * @param sessionId 所属会话
     * @param content   AI 回复的完整内容
     * @param aiModel   使用的模型名（"openai" / "deepseek" 等）
     */
    public ConsultationMessage saveAiMessage(Long sessionId, String content, String aiModel) {
        ConsultationMessage message = ConsultationMessage.builder()
                .sessionId(sessionId)
                .senderType(2)          // 写死 2=AI助手（和 saveUserMessage 的 1=用户 对应）
                .messageType(1)         // 文本
                .content(content)
                .aiModel(aiModel)        // AI 回复才有这个字段，用户消息时为 null
                .createdAt(LocalDateTime.now())
                .build();

        consultationMessageMapper.insert(message);
        return message;
    }
}
