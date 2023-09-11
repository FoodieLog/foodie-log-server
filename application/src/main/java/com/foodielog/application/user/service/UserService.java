package com.foodielog.application.user.service;

import com.foodielog.application.user.dto.response.*;
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
import com.foodielog.server.user.repository.UserRepository;
import com.foodielog.server.user.type.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final FeedRepository feedRepository;
    private final MediaRepository mediaRepository;
    private final FeedLikeRepository feedLikeRepository;
    private final RestaurantLikeRepository restaurantLikeRepository;
    private final ReplyRepository replyRepository;
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public UserProfileResp getProfile(Long userId) {
        User user = validationUserId(userId);

        Long feedCount = feedRepository.countByUserAndStatus(user, ContentStatus.NORMAL);
        Long follower = followRepository.countByFollowedId(user);
        Long following = followRepository.countByFollowingId(user);

        return new UserProfileResp(user, feedCount, follower, following);
    }

    @Transactional(readOnly = true)
    public UserThumbnailResp getThumbnail(Long userId, Long feedId, Pageable pageable) {
        User user = validationUserId(userId);

        List<Feed> feeds = feedRepository.getFeeds(user, feedId, ContentStatus.NORMAL, pageable);

        List<UserThumbnailResp.ThumbnailDTO> thumbnailDTO = feeds.stream()
                .map(UserThumbnailResp.ThumbnailDTO::new)
                .collect(Collectors.toList());

        return new UserThumbnailResp(thumbnailDTO);
    }

    @Transactional(readOnly = true)
    public UserFeedListResp getFeeds(Long userId, Long feedId, Pageable pageable) {
        User user = validationUserId(userId);

        List<Feed> feeds = feedRepository.getFeeds(user, feedId, ContentStatus.NORMAL, pageable);

        List<UserFeedListResp.UserFeedsDTO> userFeedsDTOList = new ArrayList<>();

        for (Feed feed : feeds) {
            List<Media> mediaList = mediaRepository.findByFeed(feed);

            List<UserFeedListResp.FeedImageDTO> feedImages = getFeedImageDTO(mediaList);
            UserFeedListResp.FeedDTO feedDTO = getFeedDTO(feed, feedImages);
            UserFeedListResp.UserRestaurantDTO userRestaurantDTO = getUserRestaurantDTO(feed);

            boolean isFollowed = followRepository.existsByFollowingIdAndFollowedId(user, feed.getUser());
            boolean isLiked = feedLikeRepository.existsByUserAndFeed(user, feed);

            userFeedsDTOList.add(new UserFeedListResp.UserFeedsDTO(feedDTO, userRestaurantDTO, isFollowed, isLiked));
        }

        return new UserFeedListResp(userFeedsDTOList);
    }

    @Transactional(readOnly = true)
    public UserRestaurantListResp getRestaurantList(Long userId, User user) {
        User feedOwner = validationUserId(userId);
        List<Feed> feeds = feedRepository.findByUserIdAndStatus(feedOwner.getId(), ContentStatus.NORMAL);

        List<UserRestaurantListResp.RestaurantListDTO> restaurantListDTOList = new ArrayList<>();

        for (Feed feed : feeds) {
            Restaurant restaurant = feed.getRestaurant();
            UserRestaurantListResp.RestaurantDTO restaurantDTO =
                    new UserRestaurantListResp.RestaurantDTO(restaurant);

            RestaurantLike restaurantLike =
                    restaurantLikeRepository.findByUserIdAndRestaurantId(user.getId(), restaurant.getId());

            UserRestaurantListResp.IsLikedDTO isLikedDTO = (restaurantLike == null)
                    ? new UserRestaurantListResp.IsLikedDTO(null, false)
                    : new UserRestaurantListResp.IsLikedDTO(restaurantLike.getId(), true);

            restaurantListDTOList.add(new UserRestaurantListResp.RestaurantListDTO(restaurantDTO, isLikedDTO));
        }

        return new UserRestaurantListResp(restaurantListDTOList);
    }

    private UserFeedListResp.UserRestaurantDTO getUserRestaurantDTO(Feed feed) {
        Restaurant restaurant = feed.getRestaurant();
        return new UserFeedListResp.UserRestaurantDTO(restaurant);
    }

    private UserFeedListResp.FeedDTO getFeedDTO(Feed feed, List<UserFeedListResp.FeedImageDTO> feedImages) {
        Long likeCount = feedLikeRepository.countByFeed(feed);
        Long replyCount = replyRepository.countByFeedAndStatus(feed, ContentStatus.NORMAL);

        return new UserFeedListResp.FeedDTO(feed, feedImages, likeCount, replyCount);
    }

    private List<UserFeedListResp.FeedImageDTO> getFeedImageDTO(List<Media> mediaList) {
        return mediaList.stream()
                .map(UserFeedListResp.FeedImageDTO::new)
                .collect(Collectors.toList());
    }

    private User validationUserId(Long userId) {
        return userRepository.findByIdAndStatus(userId, UserStatus.NORMAL)
                .orElseThrow(() -> new Exception404("에러"));
    }

    @Transactional
    public void follow(User following, Long followedId) {
        if (following.getId().equals(followedId)) {
            throw new Exception404("자신한테 팔로우 할 수 없습니다.");
        }
        User followed = validationUserId(followedId);

        boolean isFollow = getOptionalFollow(following, followed).isPresent();
        if (isFollow) {
            throw new Exception404("이미 팔로우 된 유저입니다");
        }

        Follow follow = Follow.createFollow(following, followed);
        followRepository.save(follow);

        Notification notification = Notification.createNotification(followed, NotificationType.FOLLOW, follow.getId());
        notificationRepository.save(notification);
    }

    @Transactional
    public void unFollow(User following, Long followedId) {
        if (following.getId().equals(followedId)) {
            throw new Exception404("자신을 언팔로우 할 수 없습니다.");
        }
        User followed = validationUserId(followedId);

        Follow follow = getOptionalFollow(following, followed)
                .orElseThrow(() -> new Exception404("팔로우 되지 않은 유저입니다."));

        followRepository.delete(follow);
    }

    private Optional<Follow> getOptionalFollow(User following, User followed) {
        return followRepository.findByFollowingIdAndFollowedId(following, followed);
    }

    @Transactional(readOnly = true)
    public UserSearchResp search(String keyword) {
        List<User> userList = userRepository.searchUserOrderByFollowedIdDesc(keyword);

        List<UserSearchResp.UserDTO> userDTOList = userList.stream()
                .map(UserSearchResp.UserDTO::new)
                .collect(Collectors.toList());

        return new UserSearchResp(userDTOList);
    }
}