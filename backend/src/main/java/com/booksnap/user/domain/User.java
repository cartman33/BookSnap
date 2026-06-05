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

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 255, unique = true)
  private String email;

  @Column(name = "password_hash", nullable = false, length = 255)
  private String passwordHash;

  @Column(nullable = false, length = 100)
  private String nickname;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  public static User local(String email, String passwordHash, String nickname) {
    User u = new User();
    u.email = email;
    u.passwordHash = passwordHash;
    u.nickname = (nickname == null || nickname.isBlank()) ? "사용자" : nickname;
    return u;
  }

  public void updateNickname(String nickname) {
    if (nickname != null && !nickname.isBlank()) this.nickname = nickname;
  }

  @PrePersist
  void prePersist() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }
}

