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

/**
 * Spring Security 보안 설정 클래스입니다.
 * 인증/인가 정책 및 필터 체인을 정의합니다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenProvider jwtTokenProvider)
      throws Exception {
    http
            // 1. REST API이므로 CSRF 보안, 기본 폼 로그인, HTTP Basic 인증을 비활성화합니다.
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)

            // 2. JWT 기반 인증을 사용하므로 서버에서 세션을 생성하거나 유지하지 않습니다. (무상태성)
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 3. API 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                    // 인증이 필요 없는 경로 설정 (회원가입, 로그인, 헬스체크 등)
                    .requestMatchers("/api/auth/**", "/actuator/**").permitAll()
                    // 그 외 모든 요청은 인증된 사용자만 접근 가능
                    .anyRequest().authenticated()
            )
            // 4. JWT 인증 필터를 UsernamePasswordAuthenticationFilter 이전에 추가합니다.
            .addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}