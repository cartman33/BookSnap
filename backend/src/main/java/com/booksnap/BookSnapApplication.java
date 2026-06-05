package com.booksnap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * BookSnap 애플리케이션의 메인 엔트리 포인트 클래스입니다.
 * Spring Boot 설정을 자동화하고 애플리케이션을 구동합니다.
 */
@SpringBootApplication
public class BookSnapApplication {
  public static void main(String[] args) {
    SpringApplication.run(BookSnapApplication.class, args);
  }
}

