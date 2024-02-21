package com.foodielog.management.user.controller;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodielog.management.user.controller.dto.LoginReq;
import com.foodielog.management.user.service.UserAuthService;
import com.foodielog.management.user.service.dto.LoginResp;
import com.foodielog.management.user.service.dto.ReissueResp;
import com.foodielog.server._core.security.jwt.JwtTokenProvider;
import com.foodielog.server._core.util.ApiUtils;
import com.foodielog.server._core.util.CookieUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/auth")
@RestController
public class UserAuthController {
	private final JwtTokenProvider jwtTokenProvider;
	private final UserAuthService userAuthService;

	@RequestMapping("/healthcheck")
	public ResponseEntity<ApiUtils.ApiResult<Object>> healthcheck() {
		return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.OK), HttpStatus.OK);
	}

	/* 토큰 재발급*/
	@GetMapping("/reissue")
	public ResponseEntity<ApiUtils.ApiResult<ReissueResp>> reissue(
		@RequestHeader(JwtTokenProvider.HEADER) String header,
		@CookieValue(CookieUtil.NAME_REFRESH_TOKEN) String refreshToken
	) {
		String accessToken = jwtTokenProvider.resolveToken(header);
		ReissueResp response = userAuthService.reissue(accessToken, refreshToken);
		HttpHeaders headers = getCookieHeaders(response.getRefreshToken());
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.CREATED), headers, HttpStatus.CREATED);
	}

	/* 로그인 */
	@PostMapping("/login")
	public ResponseEntity<ApiUtils.ApiResult<LoginResp>> login(
		@RequestBody @Valid LoginReq request,
		Errors errors
	) {
		LoginResp response = userAuthService.login(request.toParam());
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
