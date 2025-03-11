package com.whalewatch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 필요에 따라 CSRF 설정 조정
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/posts/**", "/api/comments/**").authenticated()
                        .anyRequest().permitAll()
                )
                .httpBasic(withDefaults()); // 기본 httpBasic 설정 사용
        return http.build();
    }
}
