package com.booksnap.auth.config;

import com.booksnap.auth.jwt.JwtAuthenticationFilter;
import com.booksnap.auth.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenProvider jwtTokenProvider)
      throws Exception {
    http
            // 1. REST API이므로 기본 제공되는 웹 보안 기능들(CSRF, Form 로그인 등)을 모두 끕니다.
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)

            // 2. 모바일/토큰 기반 통신이므로 서버에 세션을 저장하지 않도록(STATELESS) 설정합니다.
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 3. API 요청 권한을 설정합니다.
            .authorizeHttpRequests(auth -> auth
                    // 카카오 로그인을 처리하는 인증 API와 헬스체크 API는 누구나 접근할 수 있게 열어둡니다.
                    .requestMatchers("/api/auth/**", "/actuator/**").permitAll()
                    // 그 외의 모든 API 요청은 '반드시 JWT 토큰이 있어야만(인증되어야만)' 통과시킵니다.
                    .anyRequest().authenticated()
            )
            .addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}