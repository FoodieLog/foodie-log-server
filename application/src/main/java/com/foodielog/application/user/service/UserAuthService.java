package com.foodielog.application.user.service;

import com.foodielog.application.user.controller.dto.LoginParam;
import com.foodielog.application.user.controller.dto.ResetPasswordParam;
import com.foodielog.application.user.controller.dto.SignUpParam;
import com.foodielog.application.user.service.dto.*;
import com.foodielog.server._core.error.ErrorMessage;
import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server._core.redis.RedisService;
import com.foodielog.server._core.s3.S3Uploader;
import com.foodielog.server._core.security.jwt.JwtTokenProvider;
import com.foodielog.server._core.smtp.MailService;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.type.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class UserAuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MailService mailService;
    private final RedisService redisService;
    private final S3Uploader s3Uploader;

    private final UserModuleService userModuleService;

    /* 토큰 재발급 */
    @Transactional(readOnly = true)
    public ReissueResp reissue(String accessToken, String refreshToken) {
        // Refresh Token 유효성 검사
        jwtTokenProvider.isTokenValid(refreshToken);

        // Access - Refresh 쌍 검증
        jwtTokenProvider.checkPair(accessToken, refreshToken);

        // 기존 토큰 무효화
        jwtTokenProvider.invalidateToken(accessToken);

        // 재발급
        User user = userModuleService.get(jwtTokenProvider.getEmail(accessToken));
        String newAT = jwtTokenProvider.createAccessToken(user);
        String newRT = jwtTokenProvider.createRefreshToken();

        // 리프레시 토큰  Redis에 저장 ( key = "RT " + Email / value = refreshToken )
        redisService.setObjectByKey(RedisService.REFRESH_TOKEN_PREFIX + user.getEmail(), newRT,
                JwtTokenProvider.EXP_REFRESH, TimeUnit.MILLISECONDS);

        return new ReissueResp(newAT, newRT);
    }

    @Transactional(readOnly = true)
    public ExistsEmailResp checkExistsEmail(String email) {
        Boolean isExists = userModuleService.isEmailExists(email);
        UserStatus userStatus = userModuleService.getStatus(email);
        return new ExistsEmailResp(email, isExists, userStatus);
    }

    /* 회원 가입 */
    @Transactional
    public SignUpResp signUp(SignUpParam parameter) {
        userModuleService.checkNewEmail(parameter.getEmail());
        userModuleService.checkNewNickName(parameter.getNickName());

        String encodedPassword = passwordEncoder.encode(parameter.getPassword());
        String storedFileUrl =
                (parameter.getFile() == null) ? null : s3Uploader.saveFile(parameter.getFile());
        User user = User.createUser(
                parameter.getEmail(), encodedPassword, parameter.getNickName(), storedFileUrl,
                parameter.getAboutMe()
        );

        user = userModuleService.save(user);

        return new SignUpResp(user.getEmail(), user.getNickName(), user.getProfileImageUrl());
    }

    /* 이메일 인증 */
    @Transactional
    public SendCodeForSignupResp sendCodeForSignUp(String email) {
        userModuleService.checkNewEmail(email);

        // 이메일 전송
        String title = "[FoodieLog] 회원 가입 이메일 인증 번호";
        String verificationCode = mailService.createVerificationCode();
        mailService.sendEmail(email, title, verificationCode);

        // 이메일 인증 번호 Redis에 저장 ( key = "VerificationCode " + Email / value = VerificationCode )
        redisService.setObjectByKey(
                RedisService.EMAIL_VERIFICATION_CODE_PREFIX + email, verificationCode, 3L,
                TimeUnit.MINUTES
        );

        return new SendCodeForSignupResp(email);
    }

    @Transactional
    public SendCodeForPasswordResp sendCodeForPassword(String email) {
        userModuleService.checkEmailExists(email);

        // 이메일 전송
        String title = "[FoodieLog] 비밀번호 찾기 이메일 인증 번호";
        String verificationCode = mailService.createVerificationCode();
        mailService.sendEmail(email, title, verificationCode);

        // 이메일 인증 번호 Redis에 저장 ( key = "VerificationCode " + Email / value = VerificationCode )
        redisService.setObjectByKey(
                RedisService.EMAIL_VERIFICATION_CODE_PREFIX + email, verificationCode, 3L,
                TimeUnit.MINUTES
        );

        return new SendCodeForPasswordResp(email);
    }

    @Transactional(readOnly = true)
    public VerifiedCodeResp verifiedCode(String email, String code) {
        String redisValue = redisService.getObjectByKey(
                RedisService.EMAIL_VERIFICATION_CODE_PREFIX + email, String.class
        );
        Boolean isVerified = redisValue.equals(code);

        return new VerifiedCodeResp(email, code, isVerified);
    }

    /* 로그인 */
    @Transactional(readOnly = true)
    public LoginResp login(LoginParam parameter) {
        User user = userModuleService.get(parameter.getEmail());

        if (!passwordEncoder.matches(parameter.getPassword(), user.getPassword())) {
            throw new Exception400("password", ErrorMessage.PASSWORD_NOT_MATCH);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken();

        // 리프레시 토큰  Redis에 저장 ( key = "RT " + Email / value = refreshToken )
        redisService.setObjectByKey(RedisService.REFRESH_TOKEN_PREFIX + user.getEmail(),
                refreshToken,
                JwtTokenProvider.EXP_REFRESH, TimeUnit.MILLISECONDS);

        return new LoginResp(user, accessToken, refreshToken);
    }

    /* 프로필 설정 */
    @Transactional(readOnly = true)
    public ExistsNickNameResp checkExistsNickName(String nickName) {
        Boolean isExists = userModuleService.isNickNameExists(nickName);
        return new ExistsNickNameResp(nickName, isExists);
    }

    /* 비밀번호 변경 */
    @Transactional
    public ResetPasswordResp resetPassword(ResetPasswordParam parameter) {
        User user = userModuleService.get(parameter.getEmail());
        user.resetPassword(passwordEncoder.encode(parameter.getPassword()));
        return new ResetPasswordResp(parameter.getEmail());
    }
}
