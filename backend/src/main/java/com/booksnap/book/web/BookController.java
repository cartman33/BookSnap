package com.booksnap.book.web;

import com.booksnap.book.service.BookSearchService;
import com.booksnap.user.domain.User;
import com.booksnap.user.domain.UserRepository;
import com.booksnap.book.domain.UserBook;
import com.booksnap.book.domain.UserBookRepository;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 도서 검색 및 서재 등록 관련 API를 제공하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

  private final BookSearchService bookSearchService;
  private final UserRepository userRepository;
  private final UserBookRepository userBookRepository;

  public BookController(
      BookSearchService bookSearchService, 
      UserRepository userRepository,
      UserBookRepository userBookRepository) {
    this.bookSearchService = bookSearchService;
    this.userRepository = userRepository;
    this.userBookRepository = userBookRepository;
  }

  /**
   * 카카오 API를 통해 도서를 검색합니다. (프록시)
   */
  @GetMapping("/search")
  public ResponseEntity<List<BookSearchService.BookResponse>> search(@RequestParam String query) {
    return ResponseEntity.ok(bookSearchService.search(query));
  }

  /**
   * 검색된 도서를 사용자의 서재에 추가합니다.
   */
  @PostMapping("/library")
  public ResponseEntity<Void> addToLibrary(Principal principal, @RequestBody BookSearchService.BookRequest req) {
    Long userId = Long.parseLong(principal.getName());
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    bookSearchService.addToLibrary(user, req);
    return ResponseEntity.ok().build();
  }

  /**
   * 내 서재 목록을 조회합니다.
   */
  @GetMapping("/library")
  public ResponseEntity<List<MyBookResponse>> getMyLibrary(Principal principal) {
    Long userId = Long.parseLong(principal.getName());
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    List<UserBook> myBooks = userBookRepository.findAllByUser(user);
    List<MyBookResponse> response = myBooks.stream()
        .map(ub -> new MyBookResponse(
            ub.getId(),
            ub.getBook().getIsbn(),
            ub.getBook().getTitle(),
            ub.getBook().getAuthor(),
            ub.getBook().getThumbnailUrl(),
            ub.getStatus().name()
        ))
        .collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }

  public record MyBookResponse(
      Long userBookId, 
      String isbn, 
      String title, 
      String author, 
      String thumbnailUrl, 
      String status) {}
}
