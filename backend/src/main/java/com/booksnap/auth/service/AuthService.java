package com.booksnap.auth.service;

import com.booksnap.auth.jwt.JwtTokenProvider;
import com.booksnap.user.domain.User;
import com.booksnap.user.domain.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  @Transactional
  public TokenPair signup(String email, String rawPassword, String nickname) {
    if (userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }

    String passwordHash = passwordEncoder.encode(rawPassword);
    User user = userRepository.save(User.local(email, passwordHash, nickname));
    String accessToken = jwtTokenProvider.createAccessToken(user.getId());
    String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
    return new TokenPair(accessToken, refreshToken);
  }

  @Transactional(readOnly = true)
  public TokenPair login(String email, String rawPassword) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

    if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
      throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    String accessToken = jwtTokenProvider.createAccessToken(user.getId());
    String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
    return new TokenPair(accessToken, refreshToken);
  }

  public record TokenPair(String accessToken, String refreshToken) {}
}

