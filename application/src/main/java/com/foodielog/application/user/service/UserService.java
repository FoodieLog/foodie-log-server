package com.foodielog.application.user.service;

import com.foodielog.application.user.dto.*;
import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.entity.Media;
import com.foodielog.server.feed.repository.FeedLikeRepository;
import com.foodielog.server.feed.repository.FeedRepository;
import com.foodielog.server.feed.repository.MediaRepository;
import com.foodielog.server.reply.repository.ReplyRepository;
import com.foodielog.server.restaurant.entity.Restaurant;
import com.foodielog.server.restaurant.entity.RestaurantLike;
import com.foodielog.server.restaurant.repository.RestaurantLikeRepository;
import com.foodielog.server.user.entity.Follow;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.repository.FollowRepository;
import com.foodielog.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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

    @Transactional(readOnly = true)
    public UserRestaurantListDTO.Response getRestaurantList(Long userId, User user) {
        User feedOwner = validationUserId(userId);
        List<Feed> feeds = feedRepository.findByUserId(feedOwner.getId());

        List<UserRestaurantListDTO.Response.RestaurantListDTO> restaurantListDTOList = new ArrayList<>();

        for (Feed feed : feeds) {
            Restaurant restaurant = feed.getRestaurant();
            UserRestaurantListDTO.Response.RestaurantDTO restaurantDTO =
                    new UserRestaurantListDTO.Response.RestaurantDTO(restaurant);

            RestaurantLike restaurantLike =
                    restaurantLikeRepository.findByUserIdAndRestaurantId(user.getId(), restaurant.getId());

            UserRestaurantListDTO.Response.IsLikedDTO isLikedDTO = (restaurantLike == null)
                    ? new UserRestaurantListDTO.Response.IsLikedDTO(null, false)
                    : new UserRestaurantListDTO.Response.IsLikedDTO(restaurantLike.getId(), true);

            restaurantListDTOList.add(new UserRestaurantListDTO.Response.RestaurantListDTO(restaurantDTO, isLikedDTO));
        }

        return new UserRestaurantListDTO.Response(restaurantListDTOList);
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

    public UserSearchDTO.Response search(String keyword) {
        List<User> userList = userRepository.searchUserOrderByFollowedIdDesc(keyword);

        List<UserSearchDTO.Response.UserDTO> userDTOList = userList.stream()
                .map(UserSearchDTO.Response.UserDTO::new)
                .collect(Collectors.toList());

        return new UserSearchDTO.Response(userDTOList);
    }
}