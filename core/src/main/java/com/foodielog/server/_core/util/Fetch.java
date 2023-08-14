package com.foodielog.server._core.util;

import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class Fetch {
    public static ResponseEntity<String> kakaoTokenRequest(String url, HttpMethod method, MultiValueMap<String, String> body){
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<?> httpEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> responseEntity = rt.exchange(url, method, httpEntity, String.class);
        return responseEntity;
    }

    public static ResponseEntity<String> kakaoUserInfoRequest(String url, HttpMethod method, String accessToken){
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(accessToken);

        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = rt.exchange(url, method, httpEntity, String.class);
        return responseEntity;
    }
}
