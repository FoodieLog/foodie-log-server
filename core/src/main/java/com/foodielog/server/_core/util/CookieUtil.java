package com.foodielog.server._core.util;

import com.foodielog.server._core.security.jwt.JwtTokenProvider;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.ResponseCookie;

public class CookieUtil {
    public static final String NAME_REFRESH_TOKEN = "refreshToken";

    public static ResponseCookie getRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(NAME_REFRESH_TOKEN, refreshToken)
                .maxAge(JwtTokenProvider.EXP_REFRESH)
                .domain("www.foodielog.shop")
                .path("/")
                .secure(true) // https 환경에서만 쿠키가 발동
                .sameSite(Cookie.SameSite.NONE.attributeValue()) // 크로스 사이트에도 전송 가능
                .httpOnly(true) // 브라우저에서 접근 불가
                .build();
    }
}
