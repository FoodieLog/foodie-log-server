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
import com.foodielog.server.restaurant.repository.RestaurantRepository;
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
    private final RestaurantRepository restaurantRepository;

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

    @Transactional(readOnly = true)
    public RestaurantFeedListDTO.Response getRestaurantDetail(User user, Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new Exception404("에러"));

        RestaurantFeedListDTO.RestaurantInfoDTO restaurantInfoDTO = createRestaurantInfoDTO(restaurant, user);
        List<RestaurantFeedListDTO.RestaurantFeedsDTO> restaurantFeedsDTOList = createRestaurantFeedsDTO(restaurant, user);

        return new RestaurantFeedListDTO.Response(restaurantInfoDTO, restaurantFeedsDTOList);
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

    private RestaurantFeedListDTO.RestaurantInfoDTO createRestaurantInfoDTO(Restaurant restaurant, User user) {
        RestaurantFeedListDTO.RestaurantDTO restaurantDTO = new RestaurantFeedListDTO.RestaurantDTO(restaurant);

        RestaurantLike restaurantLike =
                restaurantLikeRepository.findByUserIdAndRestaurantId(user.getId(), restaurant.getId());

        RestaurantFeedListDTO.IsLikedDTO isLikedDTO = (restaurantLike == null)
                ? new RestaurantFeedListDTO.IsLikedDTO(null, false)
                : new RestaurantFeedListDTO.IsLikedDTO(restaurantLike.getId(), true);

        return new RestaurantFeedListDTO.RestaurantInfoDTO(restaurantDTO, isLikedDTO);
    }

    private List<RestaurantFeedListDTO.RestaurantFeedsDTO> createRestaurantFeedsDTO(Restaurant restaurant, User user) {
        RestaurantFeedListDTO.FeedRestaurantDTO feedRestaurantDTO =
                new RestaurantFeedListDTO.FeedRestaurantDTO(restaurant);

        List<RestaurantFeedListDTO.RestaurantFeedsDTO> restaurantFeedsDTOList = new ArrayList<>();
        List<Feed> feeds = feedRepository.findAllByRestaurantId(restaurant.getId());

        for (Feed feed : feeds) {
            List<Media> mediaList = mediaRepository.findByFeed(feed);
            List<RestaurantFeedListDTO.FeedImageDTO> feedImageDTOS = mediaList.stream()
                    .map(RestaurantFeedListDTO.FeedImageDTO::new)
                    .collect(Collectors.toList());

            Long likeCount = feedLikeRepository.countByFeed(feed);
            Long replyCount = replyRepository.countByFeed(feed);

            boolean isFollowed = followRepository.findByFollowingIdAndFollowedId(user, feed.getUser())
                    .isPresent();
            boolean isLiked = feedLikeRepository.findByUserId(user.getId())
                    .isPresent();

            // TODO : 공유 url을 어떻게 가져올 것인지 상의 필요.
            String share = null;

            RestaurantFeedListDTO.FeedDTO feedDTO =
                    new RestaurantFeedListDTO.FeedDTO(feed, feedImageDTOS, likeCount, replyCount, share);

            RestaurantFeedListDTO.RestaurantFeedsDTO restaurantFeedsDTO =
                    new RestaurantFeedListDTO.RestaurantFeedsDTO(feedDTO, feedRestaurantDTO, isFollowed, isLiked);

            restaurantFeedsDTOList.add(restaurantFeedsDTO);
        }
        return restaurantFeedsDTOList;
    }
}