package com.foodielog.application.user.controller;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.foodielog.application.user.dto.request.LoginReq;
import com.foodielog.application.user.dto.request.ResetPasswordReq;
import com.foodielog.application.user.dto.request.SignUpReq;
import com.foodielog.application.user.dto.response.ExistsEmailResp;
import com.foodielog.application.user.dto.response.ExistsKakaoResp;
import com.foodielog.application.user.dto.response.ExistsNickNameResp;
import com.foodielog.application.user.dto.response.KakaoLoginResp;
import com.foodielog.application.user.dto.response.LoginResp;
import com.foodielog.application.user.dto.response.ReissueResp;
import com.foodielog.application.user.dto.response.ResetPasswordResp;
import com.foodielog.application.user.dto.response.SendCodeForPasswordResp;
import com.foodielog.application.user.dto.response.SendCodeForSignupResp;
import com.foodielog.application.user.dto.response.SignUpResp;
import com.foodielog.application.user.dto.response.VerifiedCodeResp;
import com.foodielog.application.user.service.UserAuthService;
import com.foodielog.application.user.service.UserOauthService;
import com.foodielog.server._core.customValid.valid.ValidNickName;
import com.foodielog.server._core.security.jwt.JwtTokenProvider;
import com.foodielog.server._core.util.ApiUtils;
import com.foodielog.server._core.util.CookieUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Validated
@RequestMapping("/api/auth")
@RestController
public class UserAuthController {
	private final JwtTokenProvider jwtTokenProvider;
	private final UserAuthService userAuthService;
	private final UserOauthService userOauthService;

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

	/* 중복 체크 */
	@GetMapping("/exists/email")
	public ResponseEntity<ApiUtils.ApiResult<ExistsEmailResp>> checkExistsEmail(
		@RequestParam @Email String input
	) {
		ExistsEmailResp response = userAuthService.checkExistsEmail(input);
		HttpStatus httpStatus = response.getIsExists() ? HttpStatus.CONFLICT : HttpStatus.OK;
		return new ResponseEntity<>(ApiUtils.success(response, httpStatus), httpStatus);
	}

	@GetMapping("/exists/kakao")
	public ResponseEntity<ApiUtils.ApiResult<ExistsKakaoResp>> checkExistsKakao(
		@RequestParam String token
	) {
		ExistsKakaoResp response = userOauthService.checkExistsKakao(token);
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
	}

	/* 회원 가입 */
	@PostMapping("/signup")
	public ResponseEntity<ApiUtils.ApiResult<SignUpResp>> signUp(
		@RequestPart(value = "content") @Valid SignUpReq request,
		@RequestPart(value = "file", required = false) MultipartFile file,
		Errors errors
	) {
		SignUpResp response = userAuthService.signUp(request.toParamWith(file));
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.CREATED), HttpStatus.CREATED);
	}

	/* 이메일 인증 */
	@GetMapping("/email/code-requests/signup")
	public ResponseEntity<ApiUtils.ApiResult<SendCodeForSignupResp>> sendCodeForSignUp(
		@RequestParam @Email String email
	) {
		SendCodeForSignupResp response = userAuthService.sendCodeForSignUp(email);
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
	}

	@GetMapping("/email/code-requests/password")
	public ResponseEntity<ApiUtils.ApiResult<SendCodeForPasswordResp>> sendCodeForPassword(
		@RequestParam @Email String email
	) {
		SendCodeForPasswordResp response = userAuthService.sendCodeForPassword(email);
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
	}

	@GetMapping("/email/verification")
	public ResponseEntity<ApiUtils.ApiResult<VerifiedCodeResp>> verificationCode(
		@RequestParam @Email String email,
		@RequestParam @Size(min = 4, max = 4) String code
	) {
		VerifiedCodeResp response = userAuthService.verifiedCode(email, code);
		HttpStatus httpStatus = response.getIsVerified() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
		return new ResponseEntity<>(ApiUtils.success(response, httpStatus), httpStatus);
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

	@GetMapping("/login/kakao")
	public ResponseEntity<ApiUtils.ApiResult<KakaoLoginResp>> kakaoLogin(
		@RequestParam String token
	) {
		KakaoLoginResp response = userOauthService.kakaoLogin(token);
		HttpHeaders headers = getCookieHeaders(response.getRefreshToken());
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), headers, HttpStatus.OK);
	}

	private HttpHeaders getCookieHeaders(String refreshToken) {
		HttpHeaders headers = new HttpHeaders();
		ResponseCookie cookie = CookieUtil.getRefreshTokenCookie(refreshToken);
		headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
		return headers;
	}

	/* 프로필 설정 */
	@GetMapping("/exists/nickname")
	public ResponseEntity<ApiUtils.ApiResult<ExistsNickNameResp>> checkExistsNickName(
		@RequestParam @ValidNickName String input
	) {
		ExistsNickNameResp response = userAuthService.checkExistsNickName(input);
		HttpStatus httpStatus = response.getIsExists() ? HttpStatus.CONFLICT : HttpStatus.OK;
		return new ResponseEntity<>(ApiUtils.success(response, httpStatus), httpStatus);
	}

	/* 비밀번호 찾기*/
	@PutMapping("/password/reset")
	public ResponseEntity<ApiUtils.ApiResult<ResetPasswordResp>> resetPassword(
		@RequestBody @Valid ResetPasswordReq request,
		Errors errors
	) {
		ResetPasswordResp response = userAuthService.resetPassword(request.toParam());
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
	}
}
