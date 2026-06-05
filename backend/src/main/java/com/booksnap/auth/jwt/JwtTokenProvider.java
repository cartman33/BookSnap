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

@Component
public class JwtTokenProvider {

  private final JwtProperties props;
  private final SecretKey key;

  public JwtTokenProvider(JwtProperties props) {
    this.props = props;
    this.key = buildKey(props.secret());
  }

  public String createAccessToken(Long userId) {
    return createToken(
        Map.of("typ", "access", "uid", userId),
        props.accessTokenTtlSeconds());
  }

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

