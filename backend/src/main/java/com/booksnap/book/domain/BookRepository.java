package com.booksnap.book.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Book 엔티티에 대한 데이터베이스 접근 레포지토리입니다.
 */
public interface BookRepository extends JpaRepository<Book, String> {
}
