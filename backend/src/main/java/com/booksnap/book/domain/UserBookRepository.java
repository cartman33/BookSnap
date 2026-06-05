package com.booksnap.book.domain;

import com.booksnap.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * UserBook 엔티티에 대한 데이터베이스 접근 레포지토리입니다.
 */
public interface UserBookRepository extends JpaRepository<UserBook, Long> {

  /**
   * 특정 사용자의 서재 목록을 조회합니다.
   */
  List<UserBook> findAllByUser(User user);

  /**
   * 특정 사용자가 특정 책을 이미 서재에 등록했는지 확인합니다.
   */
  Optional<UserBook> findByUserAndBook(User user, Book book);
}
