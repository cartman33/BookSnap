package com.booksnap.record.domain;

import com.booksnap.book.domain.UserBook;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Record 엔티티에 대한 데이터베이스 접근 레포지토리입니다.
 */
public interface RecordRepository extends JpaRepository<Record, Long> {

  /**
   * 특정 서재 도서에 대한 기록 목록을 생성일 내림차순으로 조회합니다.
   */
  List<Record> findAllByUserBookOrderByCreatedAtDesc(UserBook userBook);
}
