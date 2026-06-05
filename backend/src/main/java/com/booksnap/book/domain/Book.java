package com.booksnap.book.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 도서 메타데이터 정보를 담는 엔티티 클래스입니다.
 * 카카오 도서 검색 API를 통해 얻은 정보를 저장합니다.
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "books")
public class Book {

  @Id
  @Column(length = 20)
  private String isbn;           // 도서 고유 번호 (ISBN-13 추천)

  @Column(nullable = false, length = 255)
  private String title;          // 도서 제목

  @Column(length = 255)
  private String author;         // 저자

  @Column(name = "thumbnail_url", length = 500)
  private String thumbnailUrl;   // 도서 표지 이미지 URL

  @Column(length = 100)
  private String publisher;      // 출판사

  public Book(String isbn, String title, String author, String thumbnailUrl, String publisher) {
    this.isbn = isbn;
    this.title = title;
    this.author = author;
    this.thumbnailUrl = thumbnailUrl;
    this.publisher = publisher;
  }
}
