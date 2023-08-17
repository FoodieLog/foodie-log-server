package com.foodielog.application.user.controller;

import com.foodielog.application.user.dto.SignUpDTO;
import com.foodielog.application.user.dto.UserRequest;
import com.foodielog.application.user.dto.UserResponse;
import com.foodielog.application.user.dto.response.SendCodeDTO;
import com.foodielog.application.user.dto.response.VerifiedCodeDTO;
import com.foodielog.application.user.service.UserAuthService;
import com.foodielog.application.user.service.UserOauthService;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/auth")
@RestController
public class UserAuthController {
    private final UserAuthService userAuthService;
    private final UserOauthService oauthUserService;

    /* 회원 가입 */
    @GetMapping("/exists/email")
    public ResponseEntity<?> checkExistsEmail(@RequestParam @Email String input) {
        Boolean isExists = userAuthService.checkExistsEmail(input);
        UserResponse.ExistsEmailDTO response = new UserResponse.ExistsEmailDTO(input);

        HttpStatus httpStatus = isExists ? HttpStatus.CONFLICT : HttpStatus.OK;
        return new ResponseEntity<>(ApiUtils.success(response, httpStatus), httpStatus);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(
            @RequestPart(value = "content") @Valid SignUpDTO.Request request,
            @RequestPart(value = "file") MultipartFile file,
            Errors errors
    ) {
        SignUpDTO.Response response = userAuthService.signUp(request, file);

        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }

    /* 이메일 인증 */
    @GetMapping("/email/code-requests/signup")
    public ResponseEntity<?> sendCode(@RequestParam @Email String email) {
        SendCodeDTO.Response response = userAuthService.sendCodeForSignUp(email);

        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/email/verification")
    public ResponseEntity<?> verificationCode(
            @RequestParam @Email String email,
            @RequestParam @Size(min = 4, max = 4) String code
    ) {
        VerifiedCodeDTO.Response response = userAuthService.verifiedCode(email, code);
        HttpStatus httpStatus = response.getIsVerified() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;

        return new ResponseEntity<>(ApiUtils.success(response, httpStatus), httpStatus);
    }

    /* 로그인 */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequest.LoginDTO loginDTO, Errors errors) {
        UserResponse.LoginDTO response = userAuthService.login(loginDTO);

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

    /* 프로필 설정 */
    @GetMapping("/exists/nickname")
    public ResponseEntity<?> checkExistsNickName(@RequestParam @ValidNickName String input) {
        Boolean isExists = userAuthService.checkExistsNickName(input);
        UserResponse.ExistsNickNameDTO response = new UserResponse.ExistsNickNameDTO(input);

        HttpStatus httpStatus = isExists ? HttpStatus.CONFLICT : HttpStatus.OK;
        return new ResponseEntity<>(ApiUtils.success(response, httpStatus), httpStatus);
    }

    private static HttpHeaders getCookieHeaders(UserResponse.LoginDTO response) {
        HttpHeaders headers = new HttpHeaders();
        ResponseCookie cookie = CookieUtil.getRefreshTokenCookie(response.getRefreshToken());
        log.info("쿠키 생성 완료: " + cookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return headers;
    }
}
