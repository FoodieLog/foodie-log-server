package com.foodielog.application.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodielog.application._core.fcm.FcmMessageProvider;
import com.foodielog.application.user.service.dto.FollowListResp;
import com.foodielog.application.user.service.dto.FollowerListResp;
import com.foodielog.application.user.service.dto.UserFeedResp;
import com.foodielog.application.user.service.dto.UserProfileResp;
import com.foodielog.application.user.service.dto.UserRestaurantListResp;
import com.foodielog.application.user.service.dto.UserSearchResp;
import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.entity.Media;
import com.foodielog.server.feed.repository.FeedLikeRepository;
import com.foodielog.server.feed.repository.FeedRepository;
import com.foodielog.server.feed.repository.MediaRepository;
import com.foodielog.server.feed.type.ContentStatus;
import com.foodielog.server.notification.entity.Notification;
import com.foodielog.server.notification.repository.NotificationRepository;
import com.foodielog.server.notification.type.NotificationType;
import com.foodielog.server.reply.repository.ReplyRepository;
import com.foodielog.server.restaurant.entity.Restaurant;
import com.foodielog.server.restaurant.entity.RestaurantLike;
import com.foodielog.server.restaurant.repository.RestaurantLikeRepository;
import com.foodielog.server.user.entity.Follow;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.repository.FollowRepository;
import com.foodielog.server.user.type.Flag;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	private final FollowRepository followRepository;
	private final FeedRepository feedRepository;
	private final MediaRepository mediaRepository;
	private final FeedLikeRepository feedLikeRepository;
	private final RestaurantLikeRepository restaurantLikeRepository;
	private final ReplyRepository replyRepository;
	private final NotificationRepository notificationRepository;

	private final FcmMessageProvider fcmMessageProvider;

	private final UserModuleService userModuleService;

	@Transactional(readOnly = true)
	public UserProfileResp getProfile(User user, Long userId) {
		User currentPageUser = userModuleService.get(userId);

		Long feedCount = feedRepository.countByUserAndStatus(currentPageUser, ContentStatus.NORMAL);
		Long follower = followRepository.countByFollowedId(currentPageUser);
		Long following = followRepository.countByFollowingId(currentPageUser);

		boolean isFollowed = followRepository.existsByFollowingIdAndFollowedId(user, currentPageUser);

		return new UserProfileResp(currentPageUser, feedCount, follower, following, isFollowed);
	}

	@Transactional(readOnly = true)
	public UserFeedResp getFeeds(Long userId, Long feedId, Pageable pageable) {
		User user = userModuleService.get(userId);

		List<Feed> feeds = feedRepository.getFeeds(user, feedId, ContentStatus.NORMAL, pageable);

		List<UserFeedResp.UserFeedsDTO> userFeedsDTOList = new ArrayList<>();

		for (Feed feed : feeds) {
			List<Media> mediaList = mediaRepository.findByFeed(feed);

			List<UserFeedResp.FeedImageDTO> feedImages = getFeedImageDTO(mediaList);
			UserFeedResp.FeedDTO feedDTO = getFeedDTO(feed, feedImages);
			UserFeedResp.UserRestaurantDTO userRestaurantDTO = getUserRestaurantDTO(feed);

			boolean isFollowed = followRepository.existsByFollowingIdAndFollowedId(user, feed.getUser());
			boolean isLiked = feedLikeRepository.existsByUserAndFeed(user, feed);

			userFeedsDTOList.add(new UserFeedResp.UserFeedsDTO(feedDTO, userRestaurantDTO, isFollowed, isLiked));
		}

		return new UserFeedResp(userFeedsDTOList);
	}

	@Transactional(readOnly = true)
	public UserRestaurantListResp getRestaurantList(Long userId, User user) {
		User feedOwner = userModuleService.get(userId);
		List<Feed> feeds = feedRepository.findByUserIdAndStatus(feedOwner.getId(), ContentStatus.NORMAL);

		List<Restaurant> RestaurantList = feeds.stream()
			.map(Feed::getRestaurant)
			.distinct()
			.collect(Collectors.toList());

		List<UserRestaurantListResp.RestaurantListDTO> restaurantListDTOList = new ArrayList<>();

		for (Restaurant restaurant : RestaurantList) {
			UserRestaurantListResp.RestaurantDTO restaurantDTO =
				new UserRestaurantListResp.RestaurantDTO(restaurant);

			RestaurantLike restaurantLike =
				restaurantLikeRepository.findByUserIdAndRestaurantId(user.getId(), restaurant.getId());

			UserRestaurantListResp.IsLikedDTO isLikedDTO = (restaurantLike == null)
				? new UserRestaurantListResp.IsLikedDTO(null, false)
				: new UserRestaurantListResp.IsLikedDTO(restaurantLike.getId(), true);

			restaurantListDTOList.add(new UserRestaurantListResp.RestaurantListDTO(restaurantDTO, isLikedDTO));
		}

		return new UserRestaurantListResp(restaurantListDTOList, feedOwner);
	}

	private UserFeedResp.UserRestaurantDTO getUserRestaurantDTO(Feed feed) {
		Restaurant restaurant = feed.getRestaurant();
		return new UserFeedResp.UserRestaurantDTO(restaurant);
	}

	private UserFeedResp.FeedDTO getFeedDTO(Feed feed, List<UserFeedResp.FeedImageDTO> feedImages) {
		Long likeCount = feedLikeRepository.countByFeed(feed);
		Long replyCount = replyRepository.countByFeedAndStatus(feed, ContentStatus.NORMAL);

		return new UserFeedResp.FeedDTO(feed, feedImages, likeCount, replyCount);
	}

	private List<UserFeedResp.FeedImageDTO> getFeedImageDTO(List<Media> mediaList) {
		return mediaList.stream()
			.map(UserFeedResp.FeedImageDTO::new)
			.collect(Collectors.toList());
	}

	@Transactional
	public void follow(User following, Long followedId) {
		if (following.getId().equals(followedId)) {
			throw new Exception404("자신한테 팔로우 할 수 없습니다.");
		}
		User followed = userModuleService.get(followedId);

		boolean isFollow = getOptionalFollow(following, followed).isPresent();
		if (isFollow) {
			throw new Exception404("이미 팔로우 된 유저입니다");
		}

		Follow follow = Follow.createFollow(following, followed);
		followRepository.save(follow);

		if (followed.getNotificationFlag() == Flag.Y) {
			Notification notification = Notification.createNotification(followed, NotificationType.FOLLOW,
				follow.getId());
			notificationRepository.save(notification);

			fcmMessageProvider.sendFollowMessage(followed.getEmail(), following.getEmail());
		}
	}

	@Transactional
	public void unFollow(User following, Long followedId) {
		if (following.getId().equals(followedId)) {
			throw new Exception404("자신을 언팔로우 할 수 없습니다.");
		}
		User followed = userModuleService.get(followedId);

		Follow follow = getOptionalFollow(following, followed)
			.orElseThrow(() -> new Exception404("팔로우 되지 않은 유저입니다."));

		followRepository.delete(follow);
	}

	private Optional<Follow> getOptionalFollow(User following, User followed) {
		return followRepository.findByFollowingIdAndFollowedId(following, followed);
	}

	@Transactional(readOnly = true)
	public UserSearchResp search(String keyword) {
		List<User> userList = userModuleService.searchUsers(keyword);

		List<UserSearchResp.UserDTO> userDTOList = userList.stream()
			.map(UserSearchResp.UserDTO::new)
			.collect(Collectors.toList());

		return new UserSearchResp(userDTOList);
	}

	@Transactional(readOnly = true)
	public FollowerListResp getFollower(User user, Long userId) {
		User owner = userModuleService.get(userId);
		List<Follow> followerList = followRepository.findByFollowedId(owner);

		List<FollowerListResp.FollowerDTO> followerDTOS = followerList.stream()
			.map(follower -> {
				boolean isFollowed = isFollowedByUser(user, follower.getFollowingId());
				return new FollowerListResp.FollowerDTO(follower.getFollowingId(), isFollowed);
			})
			.collect(Collectors.toList());

		return new FollowerListResp(followerDTOS);
	}

	@Transactional(readOnly = true)
	public FollowListResp getFollow(User user, Long userId) {
		User owner = userModuleService.get(userId);
		List<Follow> followList = followRepository.findByFollowingId(owner);

		List<FollowListResp.FollowDTO> followDTOS = followList.stream()
			.map(follow -> {
				boolean isFollowed = isFollowedByUser(user, follow.getFollowedId());
				return new FollowListResp.FollowDTO(follow.getFollowedId(), isFollowed);
			})
			.collect(Collectors.toList());

		return new FollowListResp(followDTOS);
	}

	private boolean isFollowedByUser(User user, User follow) {
		return followRepository.existsByFollowingIdAndFollowedId(user, follow);
	}
}