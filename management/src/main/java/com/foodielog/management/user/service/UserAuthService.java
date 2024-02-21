package com.foodielog.management.user.service;

import java.util.concurrent.TimeUnit;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.foodielog.management.user.controller.dto.LoginParam;
import com.foodielog.management.user.service.dto.LoginResp;
import com.foodielog.management.user.service.dto.ReissueResp;
import com.foodielog.server._core.error.ErrorMessage;
import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server._core.redis.RedisService;
import com.foodielog.server._core.security.jwt.JwtTokenProvider;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.repository.UserRepository;
import com.foodielog.server.user.type.Role;
import com.foodielog.server.user.type.UserStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserAuthService {
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisService redisService;

	private final UserRepository userRepository;

	/* 토큰 재발급 */
	public ReissueResp reissue(String accessToken, String refreshToken) {
		// Refresh Token 유효성 검사
		jwtTokenProvider.isTokenValid(refreshToken);

		// Refresh Token 검증
		Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
		String redisRT = redisService.getObjectByKey(RedisService.REFRESH_TOKEN_PREFIX
			+ authentication.getName(), String.class);
		if (!redisRT.equals(refreshToken)) {
			throw new Exception400("Refresh Token", "정보가 일치하지 않습니다.");
		}

		// 유저 상태 검사
		User user = userRepository.findByEmailAndStatus(authentication.getName(), UserStatus.NORMAL)
			.orElseThrow(() -> new Exception400("email", ErrorMessage.USER_NOT_FOUND));

		// 재발급
		String newAT = jwtTokenProvider.createAccessToken(user);
		String newRT = jwtTokenProvider.createRefreshToken();

		// 리프레시 토큰  Redis에 저장 ( key = "RT " + Email / value = refreshToken )
		redisService.setObjectByKey(RedisService.REFRESH_TOKEN_PREFIX + user.getEmail(), newRT,
			JwtTokenProvider.EXP_REFRESH, TimeUnit.MILLISECONDS);

		return new ReissueResp(newAT, newRT);
	}

	/* 로그인 */
	public LoginResp login(LoginParam parameter) {
		User user = userRepository.findByEmailAndRole(parameter.getEmail(), Role.ADMIN)
			.orElseThrow(() -> new Exception400("email", ErrorMessage.USER_NOT_FOUND));

		if (!passwordEncoder.matches(parameter.getPassword(), user.getPassword())) {
			throw new Exception400("password", ErrorMessage.PASSWORD_NOT_MATCH);
		}

		String accessToken = jwtTokenProvider.createAccessToken(user);
		String refreshToken = jwtTokenProvider.createRefreshToken();

		// 리프레시 토큰  Redis에 저장 ( key = "RT " + Email / value = refreshToken )
		redisService.setObjectByKey(RedisService.REFRESH_TOKEN_PREFIX + user.getEmail(), refreshToken,
			JwtTokenProvider.EXP_REFRESH, TimeUnit.MILLISECONDS);

		return new LoginResp(user, accessToken, refreshToken);
	}
}
