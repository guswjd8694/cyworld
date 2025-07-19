package com.hyunjoying.cyworld.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 보호 기능 비활성화
            .csrf(AbstractHttpConfigurer::disable)

            // HTTP 요청에 대한 접근 권한 설정
            .authorizeHttpRequests(auth ->  auth
                .anyRequest().permitAll()
            );

        return http.build();
    }
}