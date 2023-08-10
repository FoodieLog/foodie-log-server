package com.foodielog.server._core.kakaoApi;

import com.foodielog.server._core.error.exception.ApiException;

public interface KakaoAPI {
    KakaoApiResponse searchRestaurantsByKeyword(KakaoApiRequest kakaoApiRequest) throws ApiException;
}
