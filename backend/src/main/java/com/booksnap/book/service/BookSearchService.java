package com.booksnap.book.service;

import com.booksnap.book.domain.Book;
import com.booksnap.book.domain.BookRepository;
import com.booksnap.book.domain.BookStatus;
import com.booksnap.book.domain.UserBook;
import com.booksnap.book.domain.UserBookRepository;
import com.booksnap.book.infrastructure.KakaoBookApiClient;
import com.booksnap.user.domain.User;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 도서 검색 및 내 서재 관리를 담당하는 서비스 클래스입니다.
 */
@Service
public class BookSearchService {

  private final KakaoBookApiClient kakaoBookApiClient;
  private final BookRepository bookRepository;
  private final UserBookRepository userBookRepository;

  public BookSearchService(
      KakaoBookApiClient kakaoBookApiClient,
      BookRepository bookRepository,
      UserBookRepository userBookRepository) {
    this.kakaoBookApiClient = kakaoBookApiClient;
    this.bookRepository = bookRepository;
    this.userBookRepository = userBookRepository;
  }

  /**
   * 외부 API를 통해 도서를 검색합니다.
   */
  public List<BookResponse> search(String query) {
    List<Map<String, Object>> documents = kakaoBookApiClient.searchBooks(query);
    return documents.stream()
        .map(doc -> new BookResponse(
            (String) doc.get("isbn"),
            (String) doc.get("title"),
            ((List<String>) doc.get("authors")).stream().collect(Collectors.joining(", ")),
            (String) doc.get("thumbnail"),
            (String) doc.get("publisher")
        ))
        .collect(Collectors.toList());
  }

  /**
   * 검색된 도서를 내 서재에 등록합니다.
   */
  @Transactional
  public void addToLibrary(User user, BookRequest req) {
    // 1. 도서 메타데이터가 없으면 새로 저장
    Book book = bookRepository.findById(req.isbn())
        .orElseGet(() -> bookRepository.save(new Book(
            req.isbn(),
            req.title(),
            req.author(),
            req.thumbnailUrl(),
            req.publisher()
        )));

    // 2. 이미 서재에 등록되어 있는지 확인
    userBookRepository.findByUserAndBook(user, book)
        .ifPresent(ub -> {
          throw new IllegalArgumentException("이미 서재에 등록된 도서입니다.");
        });

    // 3. 서재에 추가
    userBookRepository.save(new UserBook(user, book, BookStatus.READING));
  }

  public record BookResponse(String isbn, String title, String author, String thumbnailUrl, String publisher) {}
  public record BookRequest(String isbn, String title, String author, String thumbnailUrl, String publisher) {}
}
