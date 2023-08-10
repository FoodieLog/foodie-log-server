package com.foodielog.application.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.foodielog.application.user.dto.UserResponse;
import com.foodielog.server._core.error.ErrorMessage;
import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server._core.security.jwt.JwtTokenProvider;
import com.foodielog.application.user.dto.UserRequest;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final UserRepository userRepository;

	public UserResponse.LoginDTO login(UserRequest.LoginDTO loginDTO) {
		User user = userRepository.findByEmail(loginDTO.getEmail())
			.orElseThrow(() -> new Exception400("email", ErrorMessage.USER_NOT_FOUND));

		if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
			throw new Exception400("password", ErrorMessage.PASSWORD_NOT_MATCH);
		}

		String accessToken = jwtTokenProvider.createAccessToken(user);
		String refreshToken = jwtTokenProvider.createRefreshToken(user);

		log.info("엑세스 토큰 생성 완료: "+accessToken);
		log.info("리프레시 토큰 생성 완료: "+refreshToken);

		return new UserResponse.LoginDTO(user, accessToken, refreshToken);
	}
}
