package com.foodielog.application.user.service;

import com.foodielog.application.user.dto.response.UserFeedListDTO;
import com.foodielog.application.user.dto.response.UserProfileDTO;
import com.foodielog.application.user.dto.response.UserThumbnailDTO;
import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.entity.Media;
import com.foodielog.server.feed.repository.FeedLikeRepository;
import com.foodielog.server.feed.repository.FeedRepository;
import com.foodielog.server.feed.repository.MediaRepository;
import com.foodielog.server.reply.repository.ReplyRepository;
import com.foodielog.server.restaurant.entity.Restaurant;
import com.foodielog.server.restaurant.repository.RestaurantLikeRepository;
import com.foodielog.server.restaurant.repository.RestaurantRepository;
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

import java.util.ArrayList;
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
	private final MediaRepository mediaRepository;
	private final FeedLikeRepository feedLikeRepository;
	private final RestaurantLikeRepository restaurantLikeRepository;
	private final ReplyRepository replyRepository;

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
	public UserProfileDTO.Response getProfile(Long userId) {
		User user = validationUserId(userId);

		Long feedCount = feedRepository.countByUser(user);
		Long follower = followRepository.countByFollowedId(user);
		Long following = followRepository.countByFollowingId(user);

		return new UserProfileDTO.Response(user, feedCount, follower, following);
	}

	@Transactional(readOnly = true)
	public UserThumbnailDTO.Response getThumbnail(Long userId, Long feedId, Pageable pageable) {
		User user = validationUserId(userId);

		List<Feed> feeds = feedRepository.getFeeds(user, feedId, pageable);

		List<UserThumbnailDTO.ThumbnailDTO> thumbnailDTO = feeds.stream()
				.map(UserThumbnailDTO.ThumbnailDTO::new)
				.collect(Collectors.toList());

		return new UserThumbnailDTO.Response(thumbnailDTO);
	}

	@Transactional(readOnly = true)
	public UserFeedListDTO.Response getFeeds(Long userId, Long feedId, Pageable pageable) {
		User user = validationUserId(userId);

		List<Feed> feeds = feedRepository.getFeeds(user, feedId, pageable);

		List<UserFeedListDTO.UserFeedsDTO> userFeedsDTOList = new ArrayList<>();

		for (Feed feed : feeds) {
			List<Media> mediaList = mediaRepository.findByFeed(feed);

			List<UserFeedListDTO.FeedImageDTO> feedImages = getFeedImageDTO(mediaList);
			UserFeedListDTO.FeedDTO feedDTO = getFeedDTO(feed, feedImages);
			UserFeedListDTO.UserRestaurantDTO userRestaurantDTO = getUserRestaurantDTO(feed);

			boolean isFollowed = followRepository.existsByFollowedId(user);
			boolean isLiked = restaurantLikeRepository.existsByUser(user);

			userFeedsDTOList.add(new UserFeedListDTO.UserFeedsDTO(feedDTO, userRestaurantDTO, isFollowed, isLiked));
		}

		return new UserFeedListDTO.Response(userFeedsDTOList);
	}

	private UserFeedListDTO.UserRestaurantDTO getUserRestaurantDTO(Feed feed) {
		Restaurant restaurant = feed.getRestaurant();
		return new UserFeedListDTO.UserRestaurantDTO(restaurant);
	}

	private UserFeedListDTO.FeedDTO getFeedDTO(Feed feed, List<UserFeedListDTO.FeedImageDTO> feedImages) {
		Long likeCount = feedLikeRepository.countByFeed(feed);
		Long replyCount = replyRepository.countByFeed(feed);
		String share = null;

		return new UserFeedListDTO.FeedDTO(feed, feedImages, likeCount, replyCount, share);
	}

	private List<UserFeedListDTO.FeedImageDTO> getFeedImageDTO(List<Media> mediaList) {
		return mediaList.stream()
				.map(UserFeedListDTO.FeedImageDTO::new)
				.collect(Collectors.toList());
	}

	private User validationUserId(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new Exception404("에러"));
	}
}