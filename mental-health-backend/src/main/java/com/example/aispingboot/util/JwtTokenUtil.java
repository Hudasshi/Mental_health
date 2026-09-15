package com.example.aispingboot.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.aispingboot.config.JwtConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.util.Date;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/14 20:45
 * @description
 */
@Component
public class JwtTokenUtil implements ApplicationContextAware {
    private static final String ISSUR="mental-health-assistant";

    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        JwtTokenUtil.applicationContext = applicationContext;
    }
    private static JwtConfig getJwtConfig() {
        return applicationContext.getBean(JwtConfig.class);
    }
    public static String generatetoken(Long userId, String username,Integer roleType){
        try{
            JwtConfig jwtConfig = getJwtConfig();
            Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
            Date expiration=new Date(System.currentTimeMillis()+jwtConfig.getExpiration());
            String token= JWT.create()
                    .withClaim("userId",userId)
                    .withClaim("username",username)
                    .withClaim("roleType",roleType)
                    .withExpiresAt(expiration)
                    .withIssuedAt(new Date())
                    .withIssuer(ISSUR)
                    .sign(algorithm);
            return token;
        }catch (Exception e){
            // 生成token异常，包装为运行时异常抛出
            throw new RuntimeException("生成token 失败: " + e);
        }
    }
}
