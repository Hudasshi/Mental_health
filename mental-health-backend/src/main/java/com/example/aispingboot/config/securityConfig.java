package com.example.aispingboot.config;

import ch.qos.logback.classic.spi.EventArgUtil;
import cn.hutool.core.text.AntPathMatcher;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.example.aispingboot.util.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author HU
 * @version 1.0
 * @date 2026/9/14 14:42
 * @description
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class securityConfig {
    //requestUser:3.使用AntPathMatcher的路径匹配方法
    //路径匹配器（Spring 提供，支持 Ant 风格通配符）
    public static final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private static final String[] PUBLIC_PATHS = {
            "/",
            "/api/user/login",
            "/api/user/add",
            "/api/psychological-chat/stream"

    };
    //requestUser：2.判断请求路径是否为公开路径
    /**
     * 判断请求路径是否为公开路径（在白名单内则放行，跳过 JWT 校验）
     * @param requestUri 请求URI
     * @return true=公开路径（跳过过滤）；false=需要走JWT校验
     */
    public static boolean isPublicPath(String requestUri){
        for (String publicPath :PUBLIC_PATHS) {
            if(antPathMatcher.match(publicPath,requestUri)){
                return true;
            }
        }
        return false;
    }
    //requestUser：7 SecurityConfig 注册过滤器 Bean
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(){
        return new JwtAuthenticationFilter();
    }

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http)throws Exception{
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->auth
                        .requestMatchers(PUBLIC_PATHS)
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )//requestUser：8 添加过滤器 `addFilterBefore(A, B)`：**把 A 过滤器放在 B 过滤器前面执行**
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
