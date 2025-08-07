package com.hyunjoying.cyworld.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // [진단용 수정] 모든 HTTP 요청을 일단 전부 허용해서
                // Security가 문제의 원인인지 아닌지를 확인합니다.
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                );

        return http.build();
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            // CSRF 보호 기능 비활성화
//            .csrf(AbstractHttpConfigurer::disable)
//
//            // 세션을 사용하지 않도록 설정
//            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//
//            // HTTP 요청에 대한 접근 권한 설정
//            .authorizeHttpRequests(authorize -> authorize
//                    .requestMatchers(HttpMethod.GET, "/users/**").permitAll()
//                    .requestMatchers(HttpMethod.GET, "/profile/**").permitAll()
//                    .requestMatchers(HttpMethod.GET, "/mini-homepage/**").permitAll()
//                    .requestMatchers(HttpMethod.GET, "/emotion/**").permitAll()
//
//                    // Swagger UI 관련 경로는 항상 모두 허용
//                    .requestMatchers(
//                            "/swagger-ui.html",
//                            "/swagger-ui/**",
//                            "/v3/api-docs",
//                            "/v3/api-docs/**",
//                            "/swagger-resources",
//                            "/swagger-resources/**",
//                            "/webjars/**"
//                    ).permitAll()
//
//                    // 위에서 허용한 GET 요청 외의 모든 요청(POST, PUT, DELETE 등)은 인증 요구
//                    .anyRequest().authenticated()
//            );
//
//        return http.build();
//    }
}