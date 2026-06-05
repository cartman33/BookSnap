package com.booksnap.book.domain;

import com.booksnap.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자의 개인 서재에 등록된 도서 정보를 담는 엔티티 클래스입니다.
 * User와 Book의 다대일 연관 관계를 맺습니다.
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "user_books")
public class UserBook {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;             // 소유 사용자

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "isbn", nullable = false)
  private Book book;             // 대상 도서

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private BookStatus status;     // 읽기 상태 (READING, COMPLETED)

  @Column(name = "added_at", nullable = false, updatable = false)
  private Instant addedAt;       // 서재 추가 일시

  public UserBook(User user, Book book, BookStatus status) {
    this.user = user;
    this.book = book;
    this.status = (status == null) ? BookStatus.READING : status;
  }

  @PrePersist
  void prePersist() {
    if (addedAt == null) {
      addedAt = Instant.now();
    }
  }

  public void updateStatus(BookStatus status) {
    this.status = status;
  }
}
