package com.foodielog.server._core.util;

import com.foodielog.server._core.kakaoApi.KakaoApiRequest;
import com.foodielog.server._core.kakaoApi.KakaoApiResponse;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

public class ExternalUtil {
    public static ResponseEntity<String> kakaoTokenRequest(String url, HttpMethod method, MultiValueMap<String, String> body) {
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<?> httpEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> responseEntity = rt.exchange(url, method, httpEntity, String.class);
        return responseEntity;
    }

    public static ResponseEntity<String> kakaoUserInfoRequest(String url, HttpMethod method, String accessToken) {
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(accessToken);

        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = rt.exchange(url, method, httpEntity, String.class);
        return responseEntity;
    }

    public static ResponseEntity<KakaoApiResponse> searchRestaurantsByKeyword(KakaoApiRequest kakaoApiRequest, String kakaoApiKey, String apiUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON); // json 형태로 전송

        RestTemplate restTemplate = new RestTemplate(); // 외부 api 호출에 요청, 응답하기 위해 사용
        HttpEntity<String> httpEntity = new HttpEntity<>(headers); // HttpEntity : HTTP 요청의 본문 내용과 헤더를 함께 담아주는 역할, get이니까 헤더만
        URI targetUrl = UriComponentsBuilder // 요청 uri 생성
                .fromUriString(apiUrl)
                .queryParams(kakaoApiRequest.toMultiValueMap()) // 쿼리 넣어주기
                .build()
                .encode(StandardCharsets.UTF_8) // 인코딩
                .toUri();

        ResponseEntity<KakaoApiResponse> responseEntity = restTemplate.exchange(
                targetUrl, HttpMethod.GET, httpEntity, KakaoApiResponse.class); // exchange() 메서드로 실제로 요청 날리고, 응답 받기.

        return responseEntity; // 응답 바디 내용 반환
    }
}
