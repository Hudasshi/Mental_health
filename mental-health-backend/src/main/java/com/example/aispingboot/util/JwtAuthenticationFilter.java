package com.example.aispingboot.util;
import cn.hutool.http.server.HttpServerRequest;
import com.example.aispingboot.common.ResultCode;
import com.example.aispingboot.config.securityConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Security;

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

        // TODO 后续补充JWT校验逻辑：
        //requestUser:11, 从请求头提取token
        String token = JwtTokenUtil.extractTokenFromRequest(request);
        //requestUser:12 验证token有效性，解析用户信息
        if(StringUtils.hasText(token)){

        }else{//没有token的话清楚旧的残留信息，然后给校验访问未授权
            clearSecurityContext();
            ResponseUtil.wirteError(response,ResultCode.ACCESS_UNAUTHORIZED);
        }
        chain.doFilter(request, response);
    }
    //清理上下文
    private  void clearSecurityContext(){
        SecurityContextHolder.clearContext();
    }
}
