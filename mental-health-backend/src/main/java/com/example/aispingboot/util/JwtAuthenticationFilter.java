package com.example.aispingboot.util;
import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.json.JSONUtil;
import com.example.aispingboot.DTO.response.UserLoginResponseDTO;
import com.example.aispingboot.common.ResultCode;
import com.example.aispingboot.config.securityConfig;
import com.example.aispingboot.enumClass.UserStatus;
import com.example.aispingboot.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Security;
import java.util.Collections;
import java.util.List;

/**
 * JWT认证过滤器
 * 继承OncePerRequestFilter，保证一次请求只执行一次过滤逻辑
 * 用于拦截请求，校验JWT令牌，完成用户身份认证
 * @author HU
 * @version 1.0
 * @date 2026/9/18 9:25
 * @description JWT身份认证过滤器，对非公开接口进行token校验
 */
//requestUser；4创建JwtAuthenticationFilter JWT 过滤器 重写方法，1.对白名单路径不经过校验，非公开就校验2.对非公开路径的请求执行JWT解析、身份认证逻辑
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Resource
    private UserService userService;

    /**
     * 判断当前请求是否不需要执行该过滤器
     * @param request http请求对象
     * @return true：不执行过滤；false：执行过滤
     */
    //requestUser：5 对白名单路径不经过校验，非公开就校验
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 获取请求URI
        String requestUri = request.getRequestURI();
        // 判断是否为配置的公开放行路径，公开路径不经过JWT校验
        return securityConfig.isPublicPath(requestUri);
    }
    //requestUser：6 对非公开路径的请求执行JWT解析、身份认证逻辑
    /**
     * 过滤器核心业务方法
     * 对非公开路径的请求执行JWT解析、身份认证逻辑
     * @param request http请求
     * @param response http响应
     * @param chain 过滤器链，放行请求到下一个过滤器
     * @throws ServletException servlet异常
     * @throws IOException IO异常
     */
    @Override
    protected void doFilterInternal
    (HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // 获取请求地址
        String requestURI = request.getRequestURI();
        // 获取请求方式 GET/POST等
        String method = request.getMethod();

        //requestUser:11, 从请求头提取token
        String token = JwtTokenUtil.extractTokenFromRequest(request);

        //requestUser:12 验证token有效性，解析用户信息
        // 判断token字符串不为空（非null、非空白字符）
        if(StringUtils.hasText(token)){
            //requestUser步骤16: 调用工具类校验并解析token，拿到封装后的校验结果对象
            JwtTokenUtil.TokenVerificationResult validationResult;
            try {
                validationResult = JwtTokenUtil.validateToken(token);
            } catch (Exception e) {
                // token 格式非法/过期/签名错误等，统一按 token 无效处理
                clearSecurityContext();
                ResponseUtil.writeError(response, ResultCode.TOKEN_INVALID);
                return;
            }
            // 判断校验结果对象不为空，并且token有效
            if (validationResult != null && validationResult.isValid()) {
                //步骤17：注入UserService 查询用户信息验证用户状态
                UserLoginResponseDTO.UserDetailResponseDTO user = userService.getUserById(validationResult.getUserId());
                System.out.println(JSONUtil.parseObj(user));

                // 步骤18：校验用户状态是否正常 → 下一步：正常则写 SecurityContext 放行，禁用则拒绝
                if (user != null && UserStatus.NORMAL.getCode().equals(user.getStatus())) {
                    // 18.1：创建Spring Security认证对象 → 下一步：构造 Authentication 并写入上下文
//Collections.singletonList（）创建一个**不可变的单元素 List**，里面只放一条权限。
//如果用户存在并且账号状态正常，就为该用户**分配一个角色权限**，封装成 Spring Security 识别的权限列表，一般用于登录认证成功后，构造 `Authentication` 对象，告诉 Spring 当前登录用户拥有什么角色。
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + validationResult.getRoleType())
                    );

// 18.2：用用户名+权限构造 Authentication → 下一步：setAuthentication 放进上下文
//UsernamePasswordAuthenticationToken**创建认证成功后的身份令牌**。用用户名和权限
                    UsernamePasswordAuthenticationToken authcation = new UsernamePasswordAuthenticationToken(
                            validationResult.getUsername(), // 用户名
                            null,                            // 密码凭证，JWT 场景不需要传
                            authorities                     // 权限列表
                    );
                    SecurityContextHolder.getContext().setAuthentication(authcation);
                    //18.3将token存储到请求属性中，把生成好的 jwtToken
                    // **存到本次 HttpServletRequest 对象里**，在**同一个请求链路**的后续代码 / 过滤器 / Controller 里，可以直接从 request 拿这个 token 使用。
                    request.setAttribute("jwtToken", token);
                } else {
                    // 18.4用户不存在/被禁用 → 清理上下文 + 返回 403
                    clearSecurityContext();
                    ResponseUtil.writeError(response, ResultCode.TOKEN_ACCESS_FORBIDDEN);
                    return;
                }
            }else {
                // token 验证失败 → 清理上下文 + 返回 401（token 无效）
                // token签名错误、过期、载荷缺失都会走到这里
                clearSecurityContext();
                ResponseUtil.writeError(response, ResultCode.TOKEN_INVALID);
                // 校验失败，直接返回响应，不再继续执行后面过滤器链
                return;
            }
        }else{
            // 没有token的话清除旧的残留信息，然后返回未授权401
            clearSecurityContext();
            ResponseUtil.writeError(response,ResultCode.ACCESS_UNAUTHORIZED);
            // 无token，直接返回响应，不继续放行
            return;
        }
        // token校验通过，放行请求，进入后续过滤器/Controller
        chain.doFilter(request, response);
    }
    //清理上下文
    private  void clearSecurityContext(){
        SecurityContextHolder.clearContext();
    }
}
