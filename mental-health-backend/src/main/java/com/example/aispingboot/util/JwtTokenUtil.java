package com.example.aispingboot.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.auth0.jwt.interfaces.Verification;
import com.example.aispingboot.config.JwtConfig;
import com.example.aispingboot.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.apache.ibatis.jdbc.Null;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/14 20:45
 * @description JWT工具类：负责生成token、从请求头提取token，后续新增token校验与解析方法
 */
@Component
public class JwtTokenUtil implements ApplicationContextAware {
    // JWT签发者标识
    private static final String ISSUR = "mental-health-assistant";

    // Spring上下文静态引用，用于获取JwtConfig配置Bean
    private static ApplicationContext applicationContext;

    /**
     * Spring容器启动时自动注入ApplicationContext上下文对象
     * @param applicationContext Spring应用上下文
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        JwtTokenUtil.applicationContext = applicationContext;
    }

    /**
     * 获取JWT配置类实例（密钥、过期时间等配置从yml读取）
     * @return JwtConfig配置对象
     */
    private static JwtConfig getJwtConfig() {
        return applicationContext.getBean(JwtConfig.class);
    }

    /**
     * 生成JWT令牌
     * @param userId 用户ID，存入JWT载荷
     * @param username 用户名，存入JWT载荷
     * @param roleType 用户角色类型，存入JWT载荷
     * @return 生成完成的JWT字符串
     */
    public static String generatetoken(Long userId, String username, Integer roleType) {
        try {
            // 获取yml中配置的jwt密钥、过期时长
            JwtConfig jwtConfig = getJwtConfig();
            // 使用HMAC256对称加密算法，加载密钥
            Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
            // 计算token过期时间：当前时间 + 配置的有效期
            Date expiration = new Date(System.currentTimeMillis() + jwtConfig.getExpiration());
            // 构建JWT
            String token = JWT.create()
                    .withClaim("userId", userId)        // 自定义载荷：用户ID
                    .withClaim("username", username)    // 自定义载荷：用户名
                    .withClaim("roleType", roleType)    // 自定义载荷：角色类型
                    .withExpiresAt(expiration)          // 设置过期时间
                    .withIssuedAt(new Date())           // 设置签发时间
                    .withIssuer(ISSUR)                  // 设置签发者
                    .sign(algorithm);                   // 使用算法签名生成token
            return token;
        } catch (Exception e) {
            // 生成token异常，包装为运行时异常抛出
            throw new RuntimeException("生成token 失败: " + e);
        }
    }

    //requestUser：10 请求中提取token,判断token对象存不存在
    /**
     * 从HttpServletRequest请求头中提取token
     * @param request http请求对象
     * @return 存在token则返回token字符串，不存在/请求对象为null返回null
     */
    public static String extractTokenFromRequest(HttpServletRequest request) {
        // 请求对象为空，直接返回null，防止空指针
        if (request == null) {
            return null;
        }
        // 获取请求头中key为token的值（前后端约定请求头名称为token）
        String tokenHeader = request.getHeader("token");
        // 判断token不为空串且不为空白字符（null、空字符串、全空格都会判定为空）
        if (StringUtils.hasText(tokenHeader)) {
            return tokenHeader;
        }
        // token不存在返回null
        return null;
    }
    @Getter
    //步骤13 创建内部静态类Token验证结果封装类，验证成功后承载解析出的用户信息
    public static class TokenVerificationResult{
        // 用户ID
        private final Long userId;
        // 用户名
        private final String username;
        // 角色类型
        private final Integer roleType;
        //验证是否有效
        private final boolean isValid;
        public TokenVerificationResult(Long userId, String username, Integer roleType, boolean isValid) {
            this.userId = userId;
            this.username = username;
            this.roleType = roleType;
            this.isValid = isValid;
        }
    }
    //步骤14 用和生成时一样的 HMAC256 密钥 + 签发者 ISSUER 构建 JWTVerifier，返回解码后的 DecodedJWT
    //验证token
    public static DecodedJWT verifyToken(String token){
        //14.1判断token非空
        if(!StringUtils.hasText(token)){
            throw new JWTVerificationException("token不存在");
        }
        //14.2取 JwtConfig 密钥 → 下一步：构造 HMAC256 算法
        JwtConfig jwtConfig = getJwtConfig();
        //jwtConfig.getSecret()从 yml 配置拿到我们自己定义的 JWT**密钥字符串**
        Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());

        //14.3 指定算法+签发者构建验证器 JWT.require校验 token 时，必须使用上面指定的 HMAC256 算法。
        // withIssuer(ISSUR)增加一条校验规则：**token 的签发者 (iss) 必须等于 ISSUR 常量**。
        // → 下一步：verify 校验并解码 token
        JWTVerifier verifier = JWT.require(algorithm).withIssuer(ISSUR).build();

        //14.4 执行校验并解码 token
        //`verify(token)` 才是真正执行校验：
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT;
    }

    //步骤15：validateToken 接着从解码对象取业务字段：userId(Long)、username(String)、roleType(Integer)。
    // 三字段都取到才封装成 `TokenVerificationResult(..., true)`，
    public static TokenVerificationResult validateToken(String token){
        //15.1 调用verifyToken解码token
        DecodedJWT jwt = verifyToken(token);
        //15.2从JWT载荷claim中读取 userId 和 username ，转为Long类型，Sting类型
        Long userId = jwt.getClaim("userId").asLong();
        String username = jwt.getClaim("username").asString();
        //15.3从JWT载荷claim中读取 roleType ，
        Integer roleType = null;
        try {
            // 尝试转为Integer类型，失败则用asString兜底,用字符串解读再转回整型
            roleType = jwt.getClaim("roleType").asInt();
        } catch (Exception e) {
            // asInt 对字符串型 claim 会抛异常，所以再用 asString 兜底
            String roleTypeStr = jwt.getClaim("roleType").asString();
            // 判断字符串非空，再转换成Integer
            if (StringUtils.hasText(roleTypeStr)) {
                roleType = Integer.valueOf(roleTypeStr);
            }
        }
        // 15.4：三个字段都取到才算解析成功 → 下一步：封装 TokenVerificationResult(true)
        // 校验必要字段全部存在，构造成功结果对象返回
        if (userId != null && StringUtils.hasText(username) && roleType != null) {
            return new TokenVerificationResult(userId, username, roleType, true);
        }
        // 有字段缺失，返回null，交给过滤器处理（判定token无效）
        return null;
    }
    //步骤19：获取当前token
    // 第1步：从当前请求上下文拿 attributes → 下一步：从 request 属性取 jwtToken
    public static String getCurrentToken() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            // 第2步：拿到当前 request → 下一步：读 "jwtToken" 属性
            HttpServletRequest request = attributes.getRequest();
            String jwtToken = (String) request.getAttribute("jwtToken");

            // 第3步：属性里有就直接返回 → 下一步：没有则走备用方案从请求头取
            if (jwtToken != null) {
                return jwtToken;
            }

            // 备用方案：从请求头直接获取
            String headerToken = extractTokenFromRequest(request);
            return headerToken;
        }
        // 上下文都没有（非 Web 请求线程）→ 返回 null
        return null;
    }
}
