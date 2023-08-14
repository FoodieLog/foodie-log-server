package com.foodielog.server._core.ExternalAPIUtil;

import com.foodielog.server._core.security.jwt.JwtTokenProvider;
import org.springframework.http.ResponseCookie;

public class CookieUtil {
    public static ResponseCookie getRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .maxAge(JwtTokenProvider.EXP_REFRESH)
                .path("/")
                .secure(true) // https 환경에서만 쿠키가 발동
                .sameSite("None") // 크로스 사이트에도 전송 가능
                .httpOnly(true) // 브라우저에서 접근 불가
                .build();
    }
}
