package com.foodielog.application.user.service;

import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.repository.FeedRepository;
import com.foodielog.server.user.repository.FollowRepository;
import org.springframework.data.domain.Pageable;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final UserRepository userRepository;
	private final FollowRepository followRepository;
	private final FeedRepository feedRepository;

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

	@Transactional(readOnly = true)
	public UserResponse.ProfileDTO getProfile(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new Exception404("에러"));

		Long feedCount = feedRepository.countByUser(user);
		Long follower = followRepository.countByFollowedId(user);
		Long following = followRepository.countByFollowingId(user);

		return new UserResponse.ProfileDTO(user, feedCount, follower, following);
	}

	@Transactional(readOnly = true)
	public UserResponse.ThumbnailListDTO getThumbnail(Long userId, Long feedId, Pageable pageable) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new Exception404("에러"));
		Feed feed = feedRepository.findById(feedId)
				.orElseThrow(() -> new Exception404("에러"));

		List<Feed> thumbnail = feedRepository.getThumbnail(user, feed.getId(), pageable);

		List<UserResponse.ThumbnailDTO> thumbnailDTO = thumbnail.stream()
				.map(UserResponse.ThumbnailDTO::new)
				.collect(Collectors.toList());

		return new UserResponse.ThumbnailListDTO(thumbnailDTO);
	}
}