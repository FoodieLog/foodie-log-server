package com.foodielog.application.restaurant.service;

import com.foodielog.application.restaurant.dto.LikedRestaurantDTO;
import com.foodielog.application.restaurant.dto.RecommendedRestaurantDTO;
import com.foodielog.application.restaurant.dto.RestaurantFeedListDTO;
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
    public LikedRestaurantDTO.Response getLikedRestaurant(User user) {
        List<RestaurantLike> restaurantLikes = restaurantLikeRepository.findByUser(user);

        List<LikedRestaurantDTO.Response.RestaurantListDTO> restaurantListDTOList = new ArrayList<>();
        for (RestaurantLike restaurantLike : restaurantLikes) {
            if (restaurantLike == null) {
                throw new Exception404("에러");
            }

            LikedRestaurantDTO.Response.RestaurantListDTO restaurantListDTO = getRestaurantListDTO(restaurantLike);
            restaurantListDTOList.add(restaurantListDTO);
        }

        return new LikedRestaurantDTO.Response(restaurantListDTOList);
    }

    @Transactional(readOnly = true)
    public RestaurantFeedListDTO.Response getRestaurantDetail(User user, Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new Exception404("에러"));

        RestaurantFeedListDTO.RestaurantInfoDTO restaurantInfoDTO = createRestaurantInfoDTO(restaurant, user);
        List<RestaurantFeedListDTO.RestaurantFeedsDTO> restaurantFeedsDTOList = createRestaurantFeedsDTO(restaurant, user);

        return new RestaurantFeedListDTO.Response(restaurantInfoDTO, restaurantFeedsDTOList);
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
    public RecommendedRestaurantDTO.Response getRecommendedRestaurant(String address) {
        List<Restaurant> restaurants = restaurantRepository.findByRoadAddressContaining(address);

        List<RecommendedRestaurantDTO.RestaurantsDTO> restaurantsDTOList = restaurants.stream()
                .map(restaurant -> createRestaurantsDTO(restaurant))
                .collect(Collectors.toList());

        return new RecommendedRestaurantDTO.Response(restaurantsDTOList);
    }

    private Restaurant validRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new Exception404("해당 맛집을 찾을 수 없습니다."));
    }

    private LikedRestaurantDTO.Response.RestaurantListDTO getRestaurantListDTO(RestaurantLike restaurantLike) {
        Restaurant restaurant = restaurantLike.getRestaurant();

        LikedRestaurantDTO.Response.RestaurantDTO restaurantDTO = new LikedRestaurantDTO.Response.RestaurantDTO(restaurant);
        LikedRestaurantDTO.Response.IsLikedDTO isLikedDTO = new LikedRestaurantDTO.Response.IsLikedDTO(restaurantLike.getId(), true);

        return new LikedRestaurantDTO.Response.RestaurantListDTO(restaurantDTO, isLikedDTO);
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
        List<Feed> feeds = feedRepository.findAllByRestaurantIdAndStatus(restaurant.getId(), ContentStatus.NORMAL);

        for (Feed feed : feeds) {
            List<Media> mediaList = mediaRepository.findByFeed(feed);
            List<RestaurantFeedListDTO.FeedImageDTO> feedImageDTOS = mediaList.stream()
                    .map(RestaurantFeedListDTO.FeedImageDTO::new)
                    .collect(Collectors.toList());

            Long likeCount = feedLikeRepository.countByFeed(feed);
            Long replyCount = replyRepository.countByFeedAndStatus(feed, ContentStatus.NORMAL);

            boolean isFollowed = followRepository.findByFollowingIdAndFollowedId(user, feed.getUser())
                    .isPresent();
            boolean isLiked = feedLikeRepository.findByUserId(user.getId())
                    .isPresent();

            String share = null;

            RestaurantFeedListDTO.FeedDTO feedDTO =
                    new RestaurantFeedListDTO.FeedDTO(feed, feedImageDTOS, likeCount, replyCount, share);

            RestaurantFeedListDTO.RestaurantFeedsDTO restaurantFeedsDTO =
                    new RestaurantFeedListDTO.RestaurantFeedsDTO(feedDTO, feedRestaurantDTO, isFollowed, isLiked);

            restaurantFeedsDTOList.add(restaurantFeedsDTO);
        }
        return restaurantFeedsDTOList;
    }

    private RecommendedRestaurantDTO.RestaurantsDTO createRestaurantsDTO(Restaurant restaurant) {
        Pageable pageable = PageRequest.of(0, 3);
        List<Feed> feeds = feedRepository.findTop3ByRestaurantId(restaurant.getId(), pageable);

        List<RecommendedRestaurantDTO.FeedsDTO> feedsDTOList = feeds.stream()
                .map(feed -> new RecommendedRestaurantDTO.FeedsDTO(feed))
                .collect(Collectors.toList());

        return new RecommendedRestaurantDTO.RestaurantsDTO(restaurant, feedsDTOList);
    }
}
