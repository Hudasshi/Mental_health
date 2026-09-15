package com.example.aispingboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aispingboot.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/9 17:24
 * @description
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
