package com.example.aispingboot.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.aispingboot.DTO.command.UserLoginCommandDTO;
import com.example.aispingboot.DTO.command.UserRegisterCommandDTO;
import com.example.aispingboot.DTO.response.UserLoginResponseDTO;
import com.example.aispingboot.common.Result;
import com.example.aispingboot.entity.UserEntity;
import com.example.aispingboot.enumClass.UserType;
import com.example.aispingboot.exception.BusinessException;
import com.example.aispingboot.mapper.UserMapper;
import com.example.aispingboot.service.convert.UserConvert;
import com.example.aispingboot.util.JwtTokenUtil;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/9 17:31
 * @description
 */
@Service
public class UserService {
    @Resource
    private UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
    // 用户登录
    public UserLoginResponseDTO login(UserLoginCommandDTO commandDTO){
        // LambdaQueryWrapper构建查询条件：用户名匹配 或 邮箱匹配
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUsername, commandDTO.getUsername())
                .or()
                .eq(UserEntity::getEmail, commandDTO.getUsername());

        // 调用 MP API 查询一条记录
        UserEntity user = userMapper.selectOne(queryWrapper);
        System.out.println(user);

        // 判断用户是否存在，不存在则抛出业务异常
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 判断密码是否正确，不正确则抛出业务异常
        String inputPassword = commandDTO.getPassword().trim();
        if(!passwordEncoder.matches(inputPassword,user.getPassword())){
            throw new BusinessException("密码错误");
        }
        //判断用户状态
        // 检查用户的状态
        if (!user.isActive()) {
            throw new BusinessException("用户已被禁用，请联系管理员");
        }

        String token= JwtTokenUtil.generatetoken(user.getId(),user.getUsername(),user.getUserType());
        System.out.println(token);
        // TODO：后续补充密码校验、token 生成逻辑
        UserLoginResponseDTO.UserDetailResponseDTO userInfo= UserConvert.entityToDetailResponse(user);
        return UserConvert.entityToLoginResponse(token, userInfo);
    }

    // 用户注册
    public UserLoginResponseDTO.UserDetailResponseDTO register(UserRegisterCommandDTO commandDTO){
        System.out.println(JSONUtil.parseObj(commandDTO));
        if(!commandDTO.getPassword().equals(commandDTO.getConfirmPassword())){
             throw new BusinessException("两次输入密码不一致");
        }
        // 判断用户名是否已存在
        LambdaQueryWrapper<UserEntity> userNameQuery = new LambdaQueryWrapper<>();
        userNameQuery.eq(UserEntity::getUsername, commandDTO.getUsername());
        if(userMapper.selectCount(userNameQuery)>0){
            throw new BusinessException("用户名已存在");
        }
        // 判断邮箱是否已存在
        LambdaQueryWrapper<UserEntity> emailQuery = new LambdaQueryWrapper<>();
        emailQuery.eq(UserEntity::getEmail,commandDTO.getEmail());
        if(userMapper.selectCount(emailQuery) > 0){
            throw new BusinessException("邮箱已存在");
        }
        // 校验用户类型是否有效
        if(!UserType.isValidCode(commandDTO.getUserType())){
            throw new BusinessException("用户类型无效");
        }
        //密码加密
        String password = commandDTO.getPassword().trim();
        String encodePassword = passwordEncoder.encode(password);

        UserEntity userEntity = UserConvert.registerCommandToEntity(commandDTO, encodePassword);
        userMapper.insert(userEntity);
        return UserConvert.entityToDetailResponse(userEntity);
    }
}
