package com.foodielog.server._core.kakaoApi;

import com.foodielog.server._core.util.ExternalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Slf4j
@Service
public class KakaoApiService {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @Value("${kakao.api.url}")
    private String apiUrl;

    @Transactional(readOnly = true)
    public KakaoApiResponse getKakaoSearchApi(String keyword) {
        KakaoApiRequest kakaoApiRequest = KakaoApiRequest.createKakaoApiRequest(keyword);
        ResponseEntity<KakaoApiResponse> kakaoApiResponse = ExternalUtil.searchRestaurantsByKeyword(kakaoApiRequest, kakaoApiKey, apiUrl);

        log.info("kakao search api 검색 시작");

        if (kakaoApiResponse == null || kakaoApiResponse.getBody().getDocuments() == null) {
            // TODO : 검색 결과가 없을 경우 반환값 합의 필요.
            return new KakaoApiResponse(new KakaoApiResponse.Meta(), Collections.emptyList());
        }

        log.info("kakao search api 검색 완료");

        return kakaoApiResponse.getBody();
    }
}
