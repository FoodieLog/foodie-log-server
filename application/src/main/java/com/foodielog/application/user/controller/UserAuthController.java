package com.foodielog.application.user.controller;

import com.foodielog.application.user.dto.request.LoginReq;
import com.foodielog.application.user.dto.request.ResetPasswordReq;
import com.foodielog.application.user.dto.request.SignUpReq;
import com.foodielog.application.user.dto.response.*;
import com.foodielog.application.user.service.UserAuthService;
import com.foodielog.application.user.service.UserOauthService;
import com.foodielog.server._core.customValid.valid.ValidNickName;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@RequiredArgsConstructor
@Validated
@RequestMapping("/auth")
@RestController
public class UserAuthController {
    private final UserAuthService userAuthService;
    private final UserOauthService oauthUserService;

    /* 토큰 재발급*/
    @GetMapping("/reissue")
    public ResponseEntity<ApiUtils.ApiResult<ReissueResp>> reissue(
            @RequestHeader(JwtTokenProvider.HEADER) String accessToken,
            @CookieValue(CookieUtil.NAME_REFRESH_TOKEN) String refreshToken
    ) {
        accessToken = accessToken.replaceAll(JwtTokenProvider.TOKEN_PREFIX, "");
        ReissueResp response = userAuthService.reissue(accessToken, refreshToken);
        HttpHeaders headers = getCookieHeaders(response.getRefreshToken());
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.CREATED), headers, HttpStatus.CREATED);
    }

    /* 회원 가입 */
    @GetMapping("/exists/email")
    public ResponseEntity<ApiUtils.ApiResult<ExistsEmailResp>> checkExistsEmail(
            @RequestParam @Email String input
    ) {
        ExistsEmailResp response = userAuthService.checkExistsEmail(input);
        HttpStatus httpStatus = response.getIsExists() ? HttpStatus.CONFLICT : HttpStatus.OK;
        return new ResponseEntity<>(ApiUtils.success(response, httpStatus), httpStatus);
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiUtils.ApiResult<SignUpResp>> signUp(
            @RequestPart(value = "content") @Valid SignUpReq request,
            @RequestPart(value = "file") MultipartFile file,
            Errors errors
    ) {
        SignUpResp response = userAuthService.signUp(request, file);
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
            @RequestBody @Valid LoginReq loginDTO,
            Errors errors
    ) {
        LoginResp response = userAuthService.login(loginDTO);
        HttpHeaders headers = getCookieHeaders(response.getRefreshToken());
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), headers, HttpStatus.OK);
    }

    @GetMapping("/login/kakao")
    public ResponseEntity<ApiUtils.ApiResult<KakaoLoginResp>> kakaoLogin(
            @RequestParam String token
    ) {
        KakaoLoginResp response = oauthUserService.kakaoLogin(token);
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
        ResetPasswordResp response = userAuthService.resetPassword(request);
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }
}
