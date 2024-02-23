package com.foodielog.application.user.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodielog.application.user.service.dto.ExistsKakaoResp;
import com.foodielog.application.user.service.dto.KakaoLoginResp;
import com.foodielog.server._core.error.exception.Exception401;
import com.foodielog.server._core.error.exception.Exception500;
import com.foodielog.server._core.redis.RedisService;
import com.foodielog.server._core.security.jwt.JwtTokenProvider;
import com.foodielog.server._core.util.ExternalUtil;
import com.foodielog.server._core.util.JsonConverter;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.type.ProviderType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserOauthService {
	@Value("${kakao.login.user-info-uri}")
	private String KAKAO_USER_INFO_URI;

	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisService redisService;
	private final JsonConverter jsonConverter;

	private final UserModuleService userModuleService;

	@Transactional(readOnly = true)
	public ExistsKakaoResp checkExistsKakao(String token) {
		KakaoLoginResp.kakaoApiResp.UserInfo kakaoUserInfo = getKakaoUserInfo(token);
		KakaoLoginResp.kakaoApiResp.KakaoAccount kakaoAccount = kakaoUserInfo.getKakaoAccount();

		Boolean isExists = userModuleService.isEmailExists(kakaoAccount.getEmail());
		return new ExistsKakaoResp(token, isExists);
	}

	@Transactional
	public KakaoLoginResp kakaoLogin(String token) {
		KakaoLoginResp.kakaoApiResp.UserInfo kakaoUserInfo = getKakaoUserInfo(token);
		KakaoLoginResp.kakaoApiResp.KakaoAccount kakaoAccount = kakaoUserInfo.getKakaoAccount();

		if (!kakaoAccount.getIsEmailValid()) {
			throw new Exception401("로그인 불가: 카카오 계정에 연결된 이메일이 유효하지 않습니다.");
		}

		// 회원 정보가 없으면 회원가입
		if (!userModuleService.isEmailExists(kakaoAccount.getEmail())) {
			String encodedRandomPassword = passwordEncoder.encode(UUID.randomUUID().toString());
			User user = User.createSocialUser(
				kakaoUserInfo.getId(), kakaoAccount.getEmail(), encodedRandomPassword, ProviderType.KAKAO
			);

			userModuleService.save(user);
		}

		User loginUser = userModuleService.get(kakaoAccount.getEmail());

		String accessToken = jwtTokenProvider.createAccessToken(loginUser);
		String refreshToken = jwtTokenProvider.createRefreshToken();

		// 리프레시 토큰  Redis에 저장 ( key = "RT " + Email / value = refreshToken )
		redisService.setObjectByKey(RedisService.REFRESH_TOKEN_PREFIX + loginUser.getEmail(), refreshToken,
			JwtTokenProvider.EXP_REFRESH, TimeUnit.MILLISECONDS);

		return new KakaoLoginResp(loginUser, accessToken, refreshToken, token);
	}

	private KakaoLoginResp.kakaoApiResp.UserInfo getKakaoUserInfo(String token) {
		ResponseEntity<String> userInfoResponse = ExternalUtil.kakaoUserInfoRequest(KAKAO_USER_INFO_URI,
			HttpMethod.POST, token);

		if (!userInfoResponse.getStatusCode().equals(HttpStatus.OK)) {
			throw new Exception500("카카오 로그인: " + userInfoResponse.getBody());
		}

		return jsonConverter.jsonToObject(userInfoResponse.getBody(), KakaoLoginResp.kakaoApiResp.UserInfo.class);
	}
}
