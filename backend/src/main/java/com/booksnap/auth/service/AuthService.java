package com.booksnap.auth.service;

import com.booksnap.auth.jwt.JwtTokenProvider;
import com.booksnap.user.domain.User;
import com.booksnap.user.domain.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원가입 및 로그인 등 인증 관련 비즈니스 로직을 처리하는 서비스입니다.
 */
@Service
public class AuthService {

  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final PasswordEncoder passwordEncoder;

  public AuthService(
      UserRepository userRepository, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.jwtTokenProvider = jwtTokenProvider;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * 새로운 사용자를 등록하고 토큰 쌍을 발급합니다.
   */
  @Transactional
  public TokenPair signup(String email, String rawPassword, String nickname) {
    // 1. 이메일 중복 체크
    if (userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }

    // 2. 비밀번호 암호화 및 사용자 저장
    String passwordHash = passwordEncoder.encode(rawPassword);
    User user = userRepository.save(User.local(email, passwordHash, nickname));

    // 3. Access/Refresh 토큰 생성
    String accessToken = jwtTokenProvider.createAccessToken(user.getId());
    String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
    return new TokenPair(accessToken, refreshToken);
  }

  /**
   * 이메일과 비밀번호를 확인하여 토큰 쌍을 발급합니다.
   */
  @Transactional(readOnly = true)
  public TokenPair login(String email, String rawPassword) {
    // 1. 사용자 존재 여부 확인
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

    // 2. 비밀번호 일치 여부 확인
    if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
      throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    // 3. 토큰 생성 및 반환
    String accessToken = jwtTokenProvider.createAccessToken(user.getId());
    String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
    return new TokenPair(accessToken, refreshToken);
  }

  /**
   * Access Token과 Refresh Token의 쌍을 담는 레코드입니다.
   */
  public record TokenPair(String accessToken, String refreshToken) {}
}

