package com.foodielog.server.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.foodielog.server._core.error.ErrorMessage;
import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server._core.security.jwt.JwtTokenProvider;
import com.foodielog.server.user.dto.UserRequest;
import com.foodielog.server.user.dto.UserResponse;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

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

		return new UserResponse.LoginDTO(user, accessToken, refreshToken);
	}
}