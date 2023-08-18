package com.foodielog.application.user.service;

import com.foodielog.application._core.smtp.MailService;
import com.foodielog.application.user.dto.*;
import com.foodielog.server._core.error.ErrorMessage;
import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server._core.error.exception.Exception500;
import com.foodielog.server._core.redis.RedisService;
import com.foodielog.server._core.s3.S3Uploader;
import com.foodielog.server._core.security.jwt.JwtTokenProvider;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAuthService {
    private static final String EMAIL_AUTH_CODE_PREFIX = "AuthCode ";

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MailService mailService;
    private final RedisService redisService;
    private final S3Uploader s3Uploader;

    private final UserRepository userRepository;

    /* 회원 가입 */
    @Transactional(readOnly = true)
    public ExistsEmailDTO.Response checkExistsEmail(String email) {
        Boolean isExists = userRepository.existsByEmail(email);
        return new ExistsEmailDTO.Response(email, isExists);
    }

    @Transactional
    public SignUpDTO.Response signUp(SignUpDTO.Request request, MultipartFile file) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new Exception400("email", "이미 가입된 이메일 입니다");
        }

        if (userRepository.existsByNickName(request.getNickName())) {
            throw new Exception400("nickName", "이미 사용 중인 닉네임 입니다");
        }

        String storedFileUrl = s3Uploader.saveFile(file);
        User user = User.createUser(request.getEmail(), request.getPassword(), request.getNickName(),
                storedFileUrl, request.getAboutMe());

        userRepository.save(user);

        return new SignUpDTO.Response(user.getEmail(), user.getNickName(), user.getProfileImageUrl());
    }


    /* 이메일 인증 */
    @Transactional
    public SendCodeDTO.ForSignUpDTO.Response sendCodeForSignUp(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new Exception400("email", "이미 가입된 이메일 입니다");
        }

        // 이메일 전송
        String title = "[FoodieLog] 회원 가입 이메일 인증 번호";
        String authCode = this.createCode();
        mailService.sendEmail(email, title, authCode);

        // 이메일 인증 번호 Redis에 저장 ( key = "AuthCode " + Email / value = AuthCode )
        redisService.setObjectByKey(EMAIL_AUTH_CODE_PREFIX + email, authCode, 3L, TimeUnit.MINUTES);

        return new SendCodeDTO.ForSignUpDTO.Response(email);
    }

    @Transactional
    public SendCodeDTO.ForPassWordDTO.Response sendCodeForPassword(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new Exception400("email", "해당 이메일 주소가 없습니다.");
        }

        // 이메일 전송
        String title = "[FoodieLog] 비밀번호 찾기 이메일 인증 번호";
        String authCode = this.createCode();
        mailService.sendEmail(email, title, authCode);

        // 이메일 인증 번호 Redis에 저장 ( key = "AuthCode " + Email / value = AuthCode )
        redisService.setObjectByKey(EMAIL_AUTH_CODE_PREFIX + email, authCode, 3L, TimeUnit.MINUTES);

        return new SendCodeDTO.ForPassWordDTO.Response(email);
    }

    @Transactional(readOnly = true)
    public VerifiedCodeDTO.Response verifiedCode(String email, String code) {
        String redisAuthCode = redisService.getObjectByKey(EMAIL_AUTH_CODE_PREFIX + email, String.class);
        Boolean isVerified = redisAuthCode.equals(code);

        return new VerifiedCodeDTO.Response(email, code, isVerified);
    }

    private String createCode() {
        try {
            Random random = SecureRandom.getInstanceStrong();
            int randomNumber = random.nextInt(9000) + 1000;

            return Integer.toString(randomNumber);
        } catch (NoSuchAlgorithmException e) {
            log.debug("이메일 인증 코드 생성 오류");
            throw new Exception500("서버 에러 #E1");
        }
    }

    /* 로그인 */
    @Transactional(readOnly = true)
    public LoginDTO.Response login(LoginDTO.Request loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new Exception400("email", ErrorMessage.USER_NOT_FOUND));

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new Exception400("password", ErrorMessage.PASSWORD_NOT_MATCH);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user);

        log.info("엑세스 토큰 생성 완료: " + accessToken);
        log.info("리프레시 토큰 생성 완료: " + refreshToken);

        return new LoginDTO.Response(user, accessToken, refreshToken);
    }

    /* 프로필 설정 */
    @Transactional(readOnly = true)
    public ExistsNickNameDTO.Response checkExistsNickName(String nickName) {
        Boolean isExists = userRepository.existsByNickName(nickName);
        return new ExistsNickNameDTO.Response(nickName, isExists);
    }

    /* 비밀번호 변경 */
    @Transactional
    public ResetPasswordDTO.Response resetPassword(ResetPasswordDTO.Request request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new Exception400("email", ErrorMessage.USER_NOT_FOUND));

        user.resetPassword(request.getPassword());

        return new ResetPasswordDTO.Response(request.getEmail());
    }
}
