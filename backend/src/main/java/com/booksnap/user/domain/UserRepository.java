package com.booksnap.user.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * User 엔티티에 대한 데이터베이스 접근 기능을 제공하는 레포지토리 인터페이스입니다.
 */
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * 이메일로 사용자를 조회합니다.
   */
  Optional<User> findByEmail(String email);

  /**
   * 해당 이메일을 사용하는 사용자가 존재하는지 확인합니다.
   */
  boolean existsByEmail(String email);
}

