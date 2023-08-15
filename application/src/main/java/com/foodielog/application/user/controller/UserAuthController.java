package com.foodielog.application.user.controller;

import com.foodielog.application.user.dto.UserRequest;
import com.foodielog.application.user.dto.UserResponse;
import com.foodielog.application.user.service.OauthUserService;
import com.foodielog.application.user.service.UserService;
import com.foodielog.server._core.util.ApiUtils;
import com.foodielog.server._core.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class UserAuthController {
    private final UserService userService;
    private final OauthUserService oauthUserService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequest.LoginDTO loginDTO, Errors errors) {
        UserResponse.LoginDTO response = userService.login(loginDTO);

        HttpHeaders headers = getCookieHeaders(response);

        return new ResponseEntity<>(ApiUtils.success(response), headers, HttpStatus.OK);
    }

    @GetMapping("/login/kakao")
    public ResponseEntity<?> kakaoLogin(String code) {
        log.info("kakao 인가 code : " + code);

        if (code == null || code.isEmpty()) {
            return new ResponseEntity<>(ApiUtils.error("카카오 로그인에 실패했습니다.", HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }

        UserResponse.LoginDTO response = oauthUserService.kakaoLogin(code);

        HttpHeaders headers = getCookieHeaders(response);

        return new ResponseEntity<>(ApiUtils.success(response), headers, HttpStatus.OK);
    }

    private static HttpHeaders getCookieHeaders(UserResponse.LoginDTO response) {
        HttpHeaders headers = new HttpHeaders();
        ResponseCookie cookie = CookieUtil.getRefreshTokenCookie(response.getRefreshToken());
        log.info("쿠키 생성 완료: " + cookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return headers;
    }
}
