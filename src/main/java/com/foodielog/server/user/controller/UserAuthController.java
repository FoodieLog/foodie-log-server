package com.foodielog.server.user.controller;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodielog.server._core.security.jwt.JwtTokenProvider;
import com.foodielog.server._core.util.ApiUtils;
import com.foodielog.server.user.dto.UserRequest;
import com.foodielog.server.user.dto.UserResponse;
import com.foodielog.server.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class UserAuthController {
	private final UserService userService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody @Valid UserRequest.LoginDTO loginDTO, Errors errors) {
		UserResponse.LoginDTO response = userService.login(loginDTO);

		HttpHeaders headers = new HttpHeaders();
		ResponseCookie cookie = getRefreshTokenCookie(response.getRefreshToken());
		headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

		return new ResponseEntity<>(ApiUtils.success(response), headers, HttpStatus.OK);
	}

	private static ResponseCookie getRefreshTokenCookie(String refreshToken) {
		return ResponseCookie.from("refreshToken", refreshToken)
			.maxAge(JwtTokenProvider.EXP_REFRESH)
			.path("/")
			.secure(true) // https 환경에서만 쿠키가 발동
			.sameSite("None") // 크로스 사이트에도 전송 가능
			.httpOnly(true) // 브라우저에서 접근 불가
			.build();
	}
}
