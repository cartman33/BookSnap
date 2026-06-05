package com.booksnap.book.infrastructure;

import com.booksnap.auth.config.KakaoProperties;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 카카오 도서 검색 API를 호출하는 클라이언트 클래스입니다.
 */
@Component
public class KakaoBookApiClient {

  private final KakaoProperties props;
  private final WebClient webClient;

  public KakaoBookApiClient(KakaoProperties props) {
    this.props = props;
    this.webClient = WebClient.builder()
        .baseUrl("https://dapi.kakao.com")
        .build();
  }

  /**
   * 키워드로 도서를 검색합니다.
   */
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> searchBooks(String query) {
    Map<String, Object> response = webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/v3/search/book")
            .queryParam("query", query)
            .build())
        .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + props.clientId())
        .retrieve()
        .bodyToMono(Map.class)
        .block();

    if (response == null || !response.containsKey("documents")) {
      return List.of();
    }

    return (List<Map<String, Object>>) response.get("documents");
  }
}
