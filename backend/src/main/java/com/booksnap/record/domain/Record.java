package com.booksnap.record.domain;

import com.booksnap.book.domain.UserBook;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자가 책에서 추출한 문장과 사진 정보를 담는 엔티티 클래스입니다.
 * 특정 UserBook(내 서재의 책)에 종속됩니다.
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "records")
public class Record {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_book_id", nullable = false)
  private UserBook userBook;     // 연결된 서재 도서

  @Column(name = "page_number")
  private Integer pageNumber;    // 책의 페이지 번호

  @Column(name = "image_url", length = 500)
  private String imageUrl;       // AWS S3에 저장된 이미지 URL

  @Lob
  @Column(name = "extracted_text", columnDefinition = "TEXT")
  private String extractedText;  // OCR로 추출하여 사용자가 선택한 문장

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;     // 기록 생성 일시

  public Record(UserBook userBook, Integer pageNumber, String imageUrl, String extractedText) {
    this.userBook = userBook;
    this.pageNumber = pageNumber;
    this.imageUrl = imageUrl;
    this.extractedText = extractedText;
  }

  @PrePersist
  void prePersist() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }
}
