package com.booksnap.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "booksnap.kakao")
public record KakaoProperties(
    String clientId,
    String clientSecret,
    String tokenUri,
    String userInfoUri) {}

