package com.booksnap.auth.kakao;

import com.booksnap.auth.config.KakaoProperties;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class KakaoApiClient {

  private final KakaoProperties props;
  private final WebClient webClient;

  public KakaoApiClient(KakaoProperties props) {
    this.props = props;
    this.webClient = WebClient.builder().build();
  }

  public KakaoTokenResponse exchangeCodeForToken(String code, String redirectUri) {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "authorization_code");
    form.add("client_id", props.clientId());
    if (props.clientSecret() != null && !props.clientSecret().isBlank()) {
      form.add("client_secret", props.clientSecret());
    }
    form.add("redirect_uri", redirectUri);
    form.add("code", code);

    return webClient
        .post()
        .uri(props.tokenUri())
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .accept(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromFormData(form))
        .retrieve()
        .bodyToMono(KakaoTokenResponse.class)
        .block();
  }

  @SuppressWarnings("unchecked")
  public KakaoUserInfo fetchUserInfo(String kakaoAccessToken) {
    Map<String, Object> body =
        webClient
            .get()
            .uri(props.userInfoUri())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoAccessToken)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Map.class)
            .block();

    if (body == null || body.get("id") == null) {
      throw new IllegalStateException("Kakao user info response is empty");
    }

    String id = String.valueOf(body.get("id"));

    String email = null;
    String nickname = null;

    Object accountObj = body.get("kakao_account");
    if (accountObj instanceof Map<?, ?> account) {
      Object emailObj = account.get("email");
      if (emailObj != null) {
        email = String.valueOf(emailObj);
      }
      Object profileObj = account.get("profile");
      if (profileObj instanceof Map<?, ?> profile) {
        Object nicknameObj = profile.get("nickname");
        if (nicknameObj != null) {
          nickname = String.valueOf(nicknameObj);
        }
      }
    }

    Object propertiesObj = body.get("properties");
    if (nickname == null && propertiesObj instanceof Map<?, ?> properties) {
      Object nicknameObj = properties.get("nickname");
      if (nicknameObj != null) {
        nickname = String.valueOf(nicknameObj);
      }
    }

    return new KakaoUserInfo(id, email, nickname);
  }
}

