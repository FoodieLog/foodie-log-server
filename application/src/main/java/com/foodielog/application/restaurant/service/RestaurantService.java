package com.foodielog.application.restaurant.service;

import com.foodielog.application.restaurant.dto.response.LikedRestaurantResp;
import com.foodielog.application.restaurant.dto.response.RecommendedRestaurantResp;
import com.foodielog.application.restaurant.dto.response.RestaurantFeedListResp;
import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.entity.Media;
import com.foodielog.server.feed.repository.FeedLikeRepository;
import com.foodielog.server.feed.repository.FeedRepository;
import com.foodielog.server.feed.repository.MediaRepository;
import com.foodielog.server.feed.type.ContentStatus;
import com.foodielog.server.reply.repository.ReplyRepository;
import com.foodielog.server.restaurant.entity.Restaurant;
import com.foodielog.server.restaurant.entity.RestaurantLike;
import com.foodielog.server.restaurant.repository.RestaurantLikeRepository;
import com.foodielog.server.restaurant.repository.RestaurantRepository;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RestaurantService {
    private final RestaurantLikeRepository restaurantLikeRepository;
    private final RestaurantRepository restaurantRepository;
    private final FeedRepository feedRepository;
    private final MediaRepository mediaRepository;
    private final FeedLikeRepository feedLikeRepository;
    private final ReplyRepository replyRepository;
    private final FollowRepository followRepository;

    @Transactional(readOnly = true)
    public LikedRestaurantResp getLikedRestaurant(User user) {
        List<RestaurantLike> restaurantLikes = restaurantLikeRepository.findByUser(user);

        List<LikedRestaurantResp.RestaurantListDTO> restaurantListDTOList = new ArrayList<>();
        for (RestaurantLike restaurantLike : restaurantLikes) {
            if (restaurantLike == null) {
                throw new Exception404("에러");
            }

            LikedRestaurantResp.RestaurantListDTO restaurantListDTO = getRestaurantListDTO(restaurantLike);
            restaurantListDTOList.add(restaurantListDTO);
        }

        return new LikedRestaurantResp(restaurantListDTOList);
    }

    @Transactional(readOnly = true)
    public RestaurantFeedListResp getRestaurantDetail(User user, Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new Exception404("에러"));

        RestaurantFeedListResp.RestaurantInfoDTO restaurantInfoDTO = createRestaurantInfoDTO(restaurant, user);
        List<RestaurantFeedListResp.RestaurantFeedsDTO> restaurantFeedsDTOList = createRestaurantFeedsDTO(restaurant, user);

        return new RestaurantFeedListResp(restaurantInfoDTO, restaurantFeedsDTOList);
    }

    @Transactional
    public void likeRestaurant(User user, Long restaurantId) {
        Restaurant restaurant = validRestaurant(restaurantId);

        if (restaurantLikeRepository.existsByUserAndRestaurant(user, restaurant)) {
            throw new Exception404("이미 좋아요를 누른 맛집입니다.");
        }

        RestaurantLike restaurantLike = RestaurantLike.createRestaurantLike(restaurant, user);
        restaurantLikeRepository.save(restaurantLike);
    }

    @Transactional
    public void unlikeRestaurant(User user, Long restaurantId) {
        Restaurant restaurant = validRestaurant(restaurantId);

        if (!restaurantLikeRepository.existsByUserAndRestaurant(user, restaurant)) {
            throw new Exception404("이미 좋아요를 취소한 맛집입니다.");
        }

        RestaurantLike restaurantLike = restaurantLikeRepository.findByUserIdAndRestaurantId(user.getId(), restaurantId);
        restaurantLikeRepository.delete(restaurantLike);
    }

    @Transactional(readOnly = true)
    public RecommendedRestaurantResp getRecommendedRestaurant(String address) {
        List<Restaurant> restaurants = restaurantRepository.findByRoadAddressContaining(address);

        List<RecommendedRestaurantResp.RestaurantsDTO> restaurantsDTOList = restaurants.stream()
                .map(this::createRestaurantsDTO)
                .collect(Collectors.toList());

        return new RecommendedRestaurantResp(restaurantsDTOList);
    }

    private Restaurant validRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new Exception404("해당 맛집을 찾을 수 없습니다."));
    }

    private LikedRestaurantResp.RestaurantListDTO getRestaurantListDTO(RestaurantLike restaurantLike) {
        Restaurant restaurant = restaurantLike.getRestaurant();

        LikedRestaurantResp.RestaurantDTO restaurantDTO = new LikedRestaurantResp.RestaurantDTO(restaurant);
        LikedRestaurantResp.IsLikedDTO isLikedDTO = new LikedRestaurantResp.IsLikedDTO(restaurantLike.getId(), true);

        return new LikedRestaurantResp.RestaurantListDTO(restaurantDTO, isLikedDTO);
    }

    private RestaurantFeedListResp.RestaurantInfoDTO createRestaurantInfoDTO(Restaurant restaurant, User user) {
        RestaurantFeedListResp.RestaurantDTO restaurantDTO = new RestaurantFeedListResp.RestaurantDTO(restaurant);

        RestaurantLike restaurantLike =
                restaurantLikeRepository.findByUserIdAndRestaurantId(user.getId(), restaurant.getId());

        RestaurantFeedListResp.IsLikedDTO isLikedDTO = (restaurantLike == null)
                ? new RestaurantFeedListResp.IsLikedDTO(null, false)
                : new RestaurantFeedListResp.IsLikedDTO(restaurantLike.getId(), true);

        return new RestaurantFeedListResp.RestaurantInfoDTO(restaurantDTO, isLikedDTO);
    }

    private List<RestaurantFeedListResp.RestaurantFeedsDTO> createRestaurantFeedsDTO(Restaurant restaurant, User user) {
        RestaurantFeedListResp.FeedRestaurantDTO feedRestaurantDTO =
                new RestaurantFeedListResp.FeedRestaurantDTO(restaurant);

        List<RestaurantFeedListResp.RestaurantFeedsDTO> restaurantFeedsDTOList = new ArrayList<>();
        List<Feed> feeds = feedRepository.findAllByRestaurantIdAndStatus(restaurant.getId(), ContentStatus.NORMAL);

        for (Feed feed : feeds) {
            List<Media> mediaList = mediaRepository.findByFeed(feed);
            List<RestaurantFeedListResp.FeedImageDTO> feedImageDTOS = mediaList.stream()
                    .map(RestaurantFeedListResp.FeedImageDTO::new)
                    .collect(Collectors.toList());

            Long likeCount = feedLikeRepository.countByFeed(feed);
            Long replyCount = replyRepository.countByFeedAndStatus(feed, ContentStatus.NORMAL);

            boolean isFollowed = followRepository.existsByFollowingIdAndFollowedId(user, feed.getUser());
            boolean isLiked = feedLikeRepository.existsByUser(user);

            RestaurantFeedListResp.FeedDTO feedDTO =
                    new RestaurantFeedListResp.FeedDTO(feed, feedImageDTOS, likeCount, replyCount);

            RestaurantFeedListResp.RestaurantFeedsDTO restaurantFeedsDTO =
                    new RestaurantFeedListResp.RestaurantFeedsDTO(feedDTO, feedRestaurantDTO, isFollowed, isLiked);

            restaurantFeedsDTOList.add(restaurantFeedsDTO);
        }
        return restaurantFeedsDTOList;
    }

    private RecommendedRestaurantResp.RestaurantsDTO createRestaurantsDTO(Restaurant restaurant) {
        Pageable pageable = PageRequest.of(0, 3);
        List<Feed> feeds = feedRepository.findTop3ByRestaurantId(restaurant.getId(), pageable);

        List<RecommendedRestaurantResp.FeedsDTO> feedsDTOList = feeds.stream()
                .map(RecommendedRestaurantResp.FeedsDTO::new)
                .collect(Collectors.toList());

        return new RecommendedRestaurantResp.RestaurantsDTO(restaurant, feedsDTOList);
    }
}
