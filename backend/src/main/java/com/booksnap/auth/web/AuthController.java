package com.booksnap.auth.web;

import com.booksnap.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/signup")
  public ResponseEntity<TokenResponse> signup(@Valid @RequestBody SignupRequest req) {
    AuthService.TokenPair pair = authService.signup(req.email(), req.password(), req.nickname());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new TokenResponse(pair.accessToken(), pair.refreshToken()));
  }

  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
    AuthService.TokenPair pair = authService.login(req.email(), req.password());
    return ResponseEntity.ok(new TokenResponse(pair.accessToken(), pair.refreshToken()));
  }
}

