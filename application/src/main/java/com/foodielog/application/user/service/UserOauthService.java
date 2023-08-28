package com.foodielog.application.user.service;

import com.foodielog.application.user.dto.response.KakaoLoginResp;
import com.foodielog.server._core.error.ErrorMessage;
import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server._core.error.exception.Exception401;
import com.foodielog.server._core.error.exception.Exception500;
import com.foodielog.server._core.redis.RedisService;
import com.foodielog.server._core.security.jwt.JwtTokenProvider;
import com.foodielog.server._core.util.ExternalUtil;
import com.foodielog.server._core.util.JsonConverter;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.repository.UserRepository;
import com.foodielog.server.user.type.ProviderType;
import com.foodielog.server.user.type.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.transaction.Transactional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserOauthService {
    @Value("${kakao.api.key}")
    private String KAKAO_API_KEY;

    @Value("${kakao.login.grant-type}")
    private String KAKAO_GRANT_TYPE;

    @Value("${kakao.login.redirect-uri}")
    private String KAKAO_REDIRECT_URI;

    @Value("${kakao.login.token-uri}")
    private String KAKAO_TOKEN_URI;

    @Value("${kakao.login.user-info-uri}")
    private String KAKAO_USER_INFO_URI;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final JsonConverter jsonConverter;
    private final UserRepository userRepository;

    @Transactional
    public KakaoLoginResp kakaoLogin(String code) {
        KakaoLoginResp.kakaoApiResp.Token kakaoAccessToken = getKakaoAccessToken(code);
        KakaoLoginResp.kakaoApiResp.UserInfo kakaoUserInfo = getKakaoUserInfo(kakaoAccessToken.getAccessToken());
        KakaoLoginResp.kakaoApiResp.KakaoAccount kakaoAccount = kakaoUserInfo.getKakaoAccount();

        if (!kakaoAccount.getIsEmailValid()) {
            throw new Exception401("로그인 불가: 카카오 계정에 연결된 이메일이 유효하지 않습니다.");
        }

        // 회원 정보가 없으면 회원가입
        if (!userRepository.existsByEmail(kakaoAccount.getEmail())) {
            String encodedRandomPassword = passwordEncoder.encode(UUID.randomUUID().toString());
            User user = User.createSocialUser(kakaoUserInfo.getId(), kakaoAccount.getEmail(), encodedRandomPassword,
                    ProviderType.KAKAO);

            userRepository.save(user);
        }

        User loginUser = userRepository.findByEmailAndStatus(kakaoAccount.getEmail(), UserStatus.NORMAL)
                .orElseThrow(() -> new Exception400("email", ErrorMessage.USER_NOT_FOUND));

        String accessToken = jwtTokenProvider.createAccessToken(loginUser);
        String refreshToken = jwtTokenProvider.createRefreshToken(loginUser);

        log.info("kakao 엑세스 토큰 생성 완료: " + accessToken);
        log.info("kakao 리프레시 토큰 생성 완료: " + refreshToken);

        // 리프레시 토큰  Redis에 저장 ( key = "RT " + Email / value = refreshToken )
        redisService.setObjectByKey(RedisService.REFRESH_TOKEN_PREFIX + loginUser.getEmail(), refreshToken,
                JwtTokenProvider.EXP_REFRESH, TimeUnit.MILLISECONDS);

        return new KakaoLoginResp(loginUser, accessToken, refreshToken, kakaoAccessToken.getAccessToken());
    }

    private KakaoLoginResp.kakaoApiResp.Token getKakaoAccessToken(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", KAKAO_GRANT_TYPE);
        body.add("client_id", KAKAO_API_KEY);
        body.add("redirect_uri", KAKAO_REDIRECT_URI);
        body.add("code", code);

        ResponseEntity<String> tokenResponse = ExternalUtil.kakaoTokenRequest(KAKAO_TOKEN_URI, HttpMethod.POST, body);

        if (!tokenResponse.getStatusCode().equals(HttpStatus.OK)) {
            throw new Exception500(tokenResponse.getBody());
        }

        return jsonConverter.jsonToObject(tokenResponse.getBody(), KakaoLoginResp.kakaoApiResp.Token.class);
    }

    private KakaoLoginResp.kakaoApiResp.UserInfo getKakaoUserInfo(String token) {
        ResponseEntity<String> userInfoResponse = ExternalUtil.kakaoUserInfoRequest(KAKAO_USER_INFO_URI, HttpMethod.POST, token);

        if (!userInfoResponse.getStatusCode().equals(HttpStatus.OK)) {
            throw new Exception500(userInfoResponse.getBody());
        }

        return jsonConverter.jsonToObject(userInfoResponse.getBody(), KakaoLoginResp.kakaoApiResp.UserInfo.class);
    }
}
