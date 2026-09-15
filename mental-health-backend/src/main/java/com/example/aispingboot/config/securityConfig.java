package com.example.aispingboot.config;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

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
    private final String[] PUBLIC_PATHS = {
            "/",
            "/api/user/login"
    };
    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http)throws Exception{
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->auth
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
