package com.example.aispingboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aispingboot.entity.ConsultationSession;
import com.example.aispingboot.service.ConsultationSessionService;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/28 22:13
 * @description
 */
@Mapper
public interface ConsultationSessionMapper extends BaseMapper<ConsultationSession> {
}
