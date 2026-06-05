package com.booksnap.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 모든 요청에서 JWT 토큰을 검사하는 필터입니다.
 * Authorization 헤더의 Bearer 토큰을 파싱하여 유효한 경우 사용자 정보를 SecurityContext에 저장합니다.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    // 1. 헤더에서 'Authorization' 값을 가져옵니다.
    String header = request.getHeader("Authorization");

    // 2. 헤더가 'Bearer '로 시작하는지 확인합니다.
    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring("Bearer ".length()).trim();

      // 3. 토큰을 검증하고 사용자 ID(userId)를 추출합니다.
      jwtTokenProvider
          .parseAccessTokenUserId(token)
          .ifPresent(
              userId -> {
                // 4. 유효한 토큰인 경우, 인증 객체를 생성하여 SecurityContext에 설정합니다.
                var auth =
                    new UsernamePasswordAuthenticationToken(userId, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);
              });
    }
    // 5. 다음 필터로 요청을 넘깁니다.
    filterChain.doFilter(request, response);
  }
}

