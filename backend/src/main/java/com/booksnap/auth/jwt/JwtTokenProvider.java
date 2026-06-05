package com.booksnap.auth.jwt;

import com.booksnap.auth.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

/**
 * JWT 토큰의 생성, 파싱, 검증을 담당하는 컴포넌트입니다.
 */
@Component
public class JwtTokenProvider {

  private final JwtProperties props;
  private final SecretKey key;

  public JwtTokenProvider(JwtProperties props) {
    this.props = props;
    this.key = buildKey(props.secret());
  }

  /**
   * 사용자 ID를 기반으로 Access Token을 생성합니다.
   */
  public String createAccessToken(Long userId) {
    return createToken(
        Map.of("typ", "access", "uid", userId),
        props.accessTokenTtlSeconds());
  }

  /**
   * 사용자 ID를 기반으로 Refresh Token을 생성합니다.
   */
  public String createRefreshToken(Long userId) {
    return createToken(
        Map.of("typ", "refresh", "uid", userId),
        props.refreshTokenTtlSeconds());
  }

  private String createToken(Map<String, Object> claims, long ttlSeconds) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(ttlSeconds);
    return Jwts.builder()
        .issuer(props.issuer())
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .claims(claims)
        .signWith(key)
        .compact();
  }

  /**
   * Access Token을 파싱하여 사용자 ID를 반환합니다.
   * 토큰이 유효하지 않거나 Access Token이 아닌 경우 빈 Optional을 반환합니다.
   */
  public Optional<Long> parseAccessTokenUserId(String token) {
    try {
      Claims claims = parse(token).getPayload();
      Object typ = claims.get("typ");
      if (!"access".equals(String.valueOf(typ))) return Optional.empty();
      Object uid = claims.get("uid");
      if (uid instanceof Number n) return Optional.of(n.longValue());
      if (uid instanceof String s) return Optional.of(Long.parseLong(s));
      return Optional.empty();
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private Jws<Claims> parse(String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
  }

  /**
   * 문자열 비밀키를 기반으로 HMAC SHA 키 객체를 생성합니다.
   */
  private static SecretKey buildKey(String secret) {
    if (secret == null) {
      throw new IllegalStateException("JWT_SECRET is required");
    }
    String trimmed = secret.trim();
    try {
      byte[] decoded = Decoders.BASE64.decode(trimmed);
      return Keys.hmacShaKeyFor(decoded);
    } catch (IllegalArgumentException e) {
      byte[] bytes = trimmed.getBytes(StandardCharsets.UTF_8);
      if (bytes.length < 32) {
        throw new IllegalStateException("JWT_SECRET must be >= 32 bytes (or base64 for >=256-bit key)");
      }
      return Keys.hmacShaKeyFor(bytes);
    }
  }
}

