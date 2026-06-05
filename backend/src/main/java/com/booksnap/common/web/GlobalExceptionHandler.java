package com.booksnap.common.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 애플리케이션 전역에서 발생하는 예외를 처리하여 표준화된 에러 응답을 반환합니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * API 에러 응답을 위한 레코드입니다.
   */
  public record ApiError(String message) {}

  /**
   * 잘못된 인자 값이 전달된 경우(IllegalArgumentException)를 처리합니다.
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(e.getMessage()));
  }

  /**
   * @Valid 검증에 실패한 경우를 처리합니다.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e) {
    // 첫 번째 검증 에러 메시지를 가져오거나 기본 메시지를 반환합니다.
    String msg =
        e.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .orElse("요청 값이 올바르지 않습니다.");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(msg));
  }
}

