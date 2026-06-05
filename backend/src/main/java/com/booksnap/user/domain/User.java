package com.booksnap.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보를 담는 엔티티 클래스입니다.
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 255, unique = true)
  private String email;          // 로그인용 이메일

  @Column(name = "password_hash", nullable = false, length = 255)
  private String passwordHash;   // 암호화된 비밀번호

  @Column(nullable = false, length = 100)
  private String nickname;       // 사용자 닉네임

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;     // 가입 일시

  /**
   * 자체 회원가입 사용자를 위한 정적 팩토리 메서드입니다.
   */
  public static User local(String email, String passwordHash, String nickname) {
    User u = new User();
    u.email = email;
    u.passwordHash = passwordHash;
    u.nickname = (nickname == null || nickname.isBlank()) ? "사용자" : nickname;
    return u;
  }

  /**
   * 닉네임을 변경합니다.
   */
  public void updateNickname(String nickname) {
    if (nickname != null && !nickname.isBlank()) this.nickname = nickname;
  }

  /**
   * 엔티티가 영속화되기 전에 실행되어 가입 일시를 설정합니다.
   */
  @PrePersist
  void prePersist() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }
}

