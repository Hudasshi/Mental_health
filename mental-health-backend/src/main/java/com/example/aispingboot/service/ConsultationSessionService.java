package com.example.aispingboot.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.example.aispingboot.DTO.command.ConsultationSessionCreateDTO;
import com.example.aispingboot.entity.ConsultationSession;
import com.example.aispingboot.entity.UserEntity;
import com.example.aispingboot.mapper.ConsultationSessionMapper;
import com.example.aispingboot.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/28 22:01
 * @description
 */
@Service
public class ConsultationSessionService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ConsultationSessionMapper consultationSessionMapper;
    public ConsultationSession createSession(Long userId, ConsultationSessionCreateDTO createDTO) {
        UserEntity user = userMapper.selectById(userId);
        if(user != null){
            ConsultationSession session = ConsultationSession.builder()
                    .userId(userId)
                    .sessionTitle(createDTO.getSessionTitle())
                    .startedAt(LocalDateTime.now())
                    .build();
            if(StrUtil.isBlank(createDTO.getSessionTitle())){
                session.setSessionTitle(String.format("AI助手 - ")
                + DateUtil.format(LocalDateTime.now(),"yyyy-MM-dd HH:mm"));
            }
            // 【步骤 4】把会话写进数据库
            // 注意：insert 成功后，MyBatis-Plus 会自动把自增主键 id 回填到 session 对象里
            consultationSessionMapper.insert(session);

            // 【步骤 5】返回带主键的实体，Controller 后续再把它转成 StreamChatSession 返回给前端
            return session;
        }
        return null;
    }
}
