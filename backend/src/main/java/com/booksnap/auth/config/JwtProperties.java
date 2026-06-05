package com.booksnap.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT(JSON Web Token) 관련 설정값을 관리하는 프로퍼티 클래스입니다.
 * application.yml의 'booksnap.jwt' 접두사를 가진 설정값들과 매핑됩니다.
 */
@ConfigurationProperties(prefix = "booksnap.jwt")
public record JwtProperties(
    String issuer,                // 토큰 발행자
    String secret,                // 토큰 서명에 사용할 비밀키
    long accessTokenTtlSeconds,   // Access Token 유효 기간 (초)
    long refreshTokenTtlSeconds) {} // Refresh Token 유효 기간 (초)

