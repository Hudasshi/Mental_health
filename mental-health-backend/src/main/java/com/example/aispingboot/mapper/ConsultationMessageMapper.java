package com.example.aispingboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aispingboot.entity.ConsultationMessage;
import com.example.aispingboot.service.ConsultationMessageService;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/29 13:47
 * @description
 */
@Mapper
public interface ConsultationMessageMapper extends BaseMapper<ConsultationMessage> {
}
