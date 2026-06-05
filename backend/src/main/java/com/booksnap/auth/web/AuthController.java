package com.booksnap.auth.web;

import com.booksnap.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증(회원가입/로그인) 관련 요청을 처리하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  /**
   * 자체 회원가입을 처리합니다.
   */
  @PostMapping("/signup")
  public ResponseEntity<TokenResponse> signup(@Valid @RequestBody SignupRequest req) {
    AuthService.TokenPair pair = authService.signup(req.email(), req.password(), req.nickname());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new TokenResponse(pair.accessToken(), pair.refreshToken()));
  }

  /**
   * 자체 로그인을 처리하고 JWT 토큰을 발급합니다.
   */
  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
    AuthService.TokenPair pair = authService.login(req.email(), req.password());
    return ResponseEntity.ok(new TokenResponse(pair.accessToken(), pair.refreshToken()));
  }
}

