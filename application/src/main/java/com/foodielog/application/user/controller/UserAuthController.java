package com.foodielog.application.user.controller;

import com.foodielog.application.user.dto.UserRequest;
import com.foodielog.application.user.dto.UserResponse;
import com.foodielog.application.user.service.OauthUserService;
import com.foodielog.application.user.service.UserService;
import com.foodielog.server._core.customValid.valid.ValidNickName;
import com.foodielog.server._core.util.ApiUtils;
import com.foodielog.server._core.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;

@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/auth")
@RestController
public class UserAuthController {
    private final UserService userService;
    private final OauthUserService oauthUserService;

    @GetMapping("/exists/email")
    public ResponseEntity<?> checkExistsEmail(@RequestParam @Email String input) {
        Boolean isExists = userService.checkExistsEmail(input);
        UserResponse.ExistsEmailDTO response = new UserResponse.ExistsEmailDTO(input);

        HttpStatus httpStatus = isExists ? HttpStatus.CONFLICT : HttpStatus.OK;
        return new ResponseEntity<>(ApiUtils.success(response, httpStatus), httpStatus);
    }

    @GetMapping("/exists/nickname")
    public ResponseEntity<?> checkExistsNickName(@RequestParam @ValidNickName String input) {
        Boolean isExists = userService.checkExistsNickName(input);
        UserResponse.ExistsNickNameDTO response = new UserResponse.ExistsNickNameDTO(input);

        HttpStatus httpStatus = isExists ? HttpStatus.CONFLICT : HttpStatus.OK;
        return new ResponseEntity<>(ApiUtils.success(response, httpStatus), httpStatus);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequest.LoginDTO loginDTO, Errors errors) {
        UserResponse.LoginDTO response = userService.login(loginDTO);

        HttpHeaders headers = getCookieHeaders(response);

        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), headers, HttpStatus.OK);
    }

    @GetMapping("/login/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestParam String code) {
        log.info("kakao 인가 code : " + code);

        UserResponse.LoginDTO response = oauthUserService.kakaoLogin(code);

        HttpHeaders headers = getCookieHeaders(response);

        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), headers, HttpStatus.OK);
    }

    private static HttpHeaders getCookieHeaders(UserResponse.LoginDTO response) {
        HttpHeaders headers = new HttpHeaders();
        ResponseCookie cookie = CookieUtil.getRefreshTokenCookie(response.getRefreshToken());
        log.info("쿠키 생성 완료: " + cookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return headers;
    }
}
