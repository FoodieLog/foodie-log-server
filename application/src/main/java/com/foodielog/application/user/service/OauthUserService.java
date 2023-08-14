package com.foodielog.application.user.service;

import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.foodielog.server._core.error.ErrorMessage;
import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server._core.error.exception.Exception401;
import com.foodielog.server._core.security.jwt.JwtTokenProvider;
import com.foodielog.application.user.dto.KakaoDTO;
import com.foodielog.application.user.dto.UserResponse;
import com.foodielog.server._core.error.exception.Exception500;
import com.foodielog.server._core.util.Fetch;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.repository.UserRepository;
import com.foodielog.server.user.type.ProviderType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class OauthUserService {
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
	private final UserRepository userRepository;

	@Transactional
	public UserResponse.LoginDTO kakaoLogin(String code) {
		KakaoDTO.Token kakaoAccessToken = getKakaoAccessToken(code);
		KakaoDTO.UserInfo kakaoUserInfo = getKakaoUserInfo(kakaoAccessToken.getAccessToken());
		KakaoDTO.KakaoAccount kakaoAccount = kakaoUserInfo.getKakaoAccount();

		if(!kakaoAccount.getIsEmailValid()){
			throw new Exception401("로그인 불가: 카카오 계정에 연결된 이메일이 유효하지 않습니다.");
		}

		// 회원 정보가 없으면 회원가입
		if(!userRepository.existsByEmail(kakaoAccount.getEmail())){
			String encodedRandomPassword = passwordEncoder.encode(UUID.randomUUID().toString());
			User user = User.createSocialUser(kakaoUserInfo.getId(),kakaoAccount.getEmail(), encodedRandomPassword,
				ProviderType.KAKAO);

			userRepository.save(user);
		}

		User loginUser = userRepository.findByEmail(kakaoAccount.getEmail())
			.orElseThrow(() -> new Exception400("email", ErrorMessage.USER_NOT_FOUND));

		String accessToken = jwtTokenProvider.createAccessToken(loginUser);
		String refreshToken = jwtTokenProvider.createRefreshToken(loginUser);

		log.info("kakao 엑세스 토큰 생성 완료: "+accessToken);
		log.info("kakao 리프레시 토큰 생성 완료: "+refreshToken);

		return new UserResponse.LoginDTO(loginUser, accessToken, refreshToken);
	}

	public KakaoDTO.Token getKakaoAccessToken(String code) {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", KAKAO_GRANT_TYPE);
		body.add("client_id", KAKAO_API_KEY);
		body.add("redirect_uri", KAKAO_REDIRECT_URI);
		body.add("code", code);

		ResponseEntity<String> tokenResponse = Fetch.kakaoTokenRequest(KAKAO_TOKEN_URI, HttpMethod.POST, body);

		if(!tokenResponse.getStatusCode().equals(HttpStatus.OK)){
			throw new Exception500(tokenResponse.getBody());
		}

		ObjectMapper om = new ObjectMapper();
		om.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		try {
			KakaoDTO.Token kakaoToken = om.readValue(tokenResponse.getBody(), KakaoDTO.Token.class);

			log.info("kakao access_token : " + kakaoToken.getAccessToken());
			return kakaoToken;
		} catch (JsonProcessingException e) {
			throw new Exception500("Kakao 로그인(1): Json 파싱 오류");
		}
	}

	public KakaoDTO.UserInfo getKakaoUserInfo(String token) {
		ResponseEntity<String> userInfoResponse = Fetch.kakaoUserInfoRequest(KAKAO_USER_INFO_URI, HttpMethod.POST, token);

		if(!userInfoResponse.getStatusCode().equals(HttpStatus.OK)){
			throw new Exception500(userInfoResponse.getBody());
		}

		ObjectMapper om = new ObjectMapper();
		om.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		try {
			KakaoDTO.UserInfo userInfo = om.readValue(userInfoResponse.getBody(), KakaoDTO.UserInfo.class);

			log.info("kakao email : " + userInfo.getKakaoAccount().getEmail());
			return userInfo;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new Exception500("Kakao 로그인(2): Json 파싱 오류");
		}
	}
}
