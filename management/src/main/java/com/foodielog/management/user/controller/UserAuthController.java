package com.foodielog.management.user.controller;

import com.foodielog.management.user.dto.request.LoginReq;
import com.foodielog.management.user.dto.response.LoginResp;
import com.foodielog.management.user.dto.response.ReissueResp;
import com.foodielog.management.user.service.UserAuthService;
import com.foodielog.server._core.security.jwt.JwtTokenProvider;
import com.foodielog.server._core.util.ApiUtils;
import com.foodielog.server._core.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@Validated
@RequestMapping("/auth")
@RestController
public class UserAuthController {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserAuthService userAuthService;

    /* 토큰 재발급*/
    @GetMapping("/reissue")
    public ResponseEntity<ApiUtils.ApiResult<ReissueResp>> reissue(
            @RequestHeader(JwtTokenProvider.HEADER) String accessToken,
            @CookieValue(CookieUtil.NAME_REFRESH_TOKEN) String refreshToken
    ) {
        accessToken = jwtTokenProvider.resolveToken(accessToken);
        ReissueResp response = userAuthService.reissue(accessToken, refreshToken);
        HttpHeaders headers = getCookieHeaders(response.getRefreshToken());
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.CREATED), headers, HttpStatus.CREATED);
    }

    /* 로그인 */
    @PostMapping("/login")
    public ResponseEntity<ApiUtils.ApiResult<LoginResp>> login(
            @RequestBody @Valid LoginReq loginDTO,
            Errors errors
    ) {
        LoginResp response = userAuthService.login(loginDTO);
        HttpHeaders headers = getCookieHeaders(response.getRefreshToken());
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), headers, HttpStatus.OK);
    }

    private HttpHeaders getCookieHeaders(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        ResponseCookie cookie = CookieUtil.getRefreshTokenCookie(refreshToken);
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return headers;
    }
}
