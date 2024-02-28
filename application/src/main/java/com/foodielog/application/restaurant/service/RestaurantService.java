package com.foodielog.application.restaurant.service;

import com.foodielog.application.feed.service.FeedModuleService;
import com.foodielog.application.feedLike.service.FeedLikeModuleService;
import com.foodielog.application.follow.service.FollowModuleService;
import com.foodielog.application.media.service.MediaModuleService;
import com.foodielog.application.reply.service.ReplyModuleService;
import com.foodielog.application.restaurant.service.dto.LikedRestaurantResp;
import com.foodielog.application.restaurant.service.dto.RecommendedRestaurantResp;
import com.foodielog.application.restaurant.service.dto.RestaurantFeedListResp;
import com.foodielog.application.restaurantLike.service.RestaurantLikeModuleService;
import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.entity.Media;
import com.foodielog.server.restaurant.entity.Restaurant;
import com.foodielog.server.restaurant.entity.RestaurantLike;
import com.foodielog.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RestaurantService {
	private final RestaurantModuleService restaurantModuleService;
	private final RestaurantLikeModuleService restaurantLikeModuleService;
	private final FeedModuleService feedModuleService;
	private final FeedLikeModuleService feedLikeModuleService;
	private final MediaModuleService mediaModuleService;
	private final ReplyModuleService replyModuleService;
	private final FollowModuleService followModuleService;

	@Transactional(readOnly = true)
	public LikedRestaurantResp getLikedRestaurant(User user) {
		List<RestaurantLike> restaurantLikes = restaurantLikeModuleService.getRestaurantLikes(user);

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
		Restaurant restaurant = validRestaurant(restaurantId);

		RestaurantFeedListResp.RestaurantInfoDTO restaurantInfoDTO = createRestaurantInfoDTO(restaurant, user);
		List<RestaurantFeedListResp.RestaurantFeedsDTO> restaurantFeedsDTOList = createRestaurantFeedsDTO(restaurant,
			user);

		return new RestaurantFeedListResp(restaurantInfoDTO, restaurantFeedsDTOList);
	}

	@Transactional
	public void likeRestaurant(User user, Long restaurantId) {
		Restaurant restaurant = validRestaurant(restaurantId);

		if (restaurantLikeModuleService.exist(user, restaurant)) {
			throw new Exception404("이미 좋아요를 누른 맛집입니다.");
		}

		RestaurantLike restaurantLike = RestaurantLike.createRestaurantLike(restaurant, user);
		restaurantLikeModuleService.save(restaurantLike);
	}

	@Transactional
	public void unlikeRestaurant(User user, Long restaurantId) {
		Restaurant restaurant = validRestaurant(restaurantId);

		if (!restaurantLikeModuleService.exist(user, restaurant)) {
			throw new Exception404("이미 좋아요를 취소한 맛집입니다.");
		}

		RestaurantLike restaurantLike = restaurantLikeModuleService.get(user, restaurant);
		restaurantLikeModuleService.delete(restaurantLike);
	}

	@Transactional(readOnly = true)
	public RecommendedRestaurantResp getRecommendedRestaurant(String address) {
		List<Restaurant> restaurants = restaurantModuleService.getByAddress(address);

		List<RecommendedRestaurantResp.RestaurantsDTO> restaurantsDTOList = restaurants.stream()
			.map(this::createRestaurantsDTO)
			.collect(Collectors.toList());

		return new RecommendedRestaurantResp(restaurantsDTOList);
	}

	private Restaurant validRestaurant(Long restaurantId) {
		return restaurantModuleService.getById(restaurantId);
	}

	private LikedRestaurantResp.RestaurantListDTO getRestaurantListDTO(RestaurantLike restaurantLike) {
		Restaurant restaurant = restaurantLike.getRestaurant();

		LikedRestaurantResp.RestaurantDTO restaurantDTO = new LikedRestaurantResp.RestaurantDTO(restaurant);
		LikedRestaurantResp.IsLikedDTO isLikedDTO = new LikedRestaurantResp.IsLikedDTO(restaurantLike.getId(), true);

		return new LikedRestaurantResp.RestaurantListDTO(restaurantDTO, isLikedDTO);
	}

	private RestaurantFeedListResp.RestaurantInfoDTO createRestaurantInfoDTO(Restaurant restaurant, User user) {
		RestaurantFeedListResp.RestaurantDTO restaurantDTO = new RestaurantFeedListResp.RestaurantDTO(restaurant);

		RestaurantLike restaurantLike = restaurantLikeModuleService.get(user, restaurant);

		RestaurantFeedListResp.IsLikedDTO isLikedDTO = (restaurantLike == null)
			? new RestaurantFeedListResp.IsLikedDTO(null, false)
			: new RestaurantFeedListResp.IsLikedDTO(restaurantLike.getId(), true);

		return new RestaurantFeedListResp.RestaurantInfoDTO(restaurantDTO, isLikedDTO);
	}

	private List<RestaurantFeedListResp.RestaurantFeedsDTO> createRestaurantFeedsDTO(Restaurant restaurant, User user) {
		RestaurantFeedListResp.FeedRestaurantDTO feedRestaurantDTO =
			new RestaurantFeedListResp.FeedRestaurantDTO(restaurant);

		List<RestaurantFeedListResp.RestaurantFeedsDTO> restaurantFeedsDTOList = new ArrayList<>();
		List<Feed> feeds = feedModuleService.getRestaurantFeeds(restaurant);

		for (Feed feed : feeds) {
			List<Media> mediaList = mediaModuleService.getMediaByFeed(feed);
			List<RestaurantFeedListResp.FeedImageDTO> feedImageDTOS = mediaList.stream()
				.map(RestaurantFeedListResp.FeedImageDTO::new)
				.collect(Collectors.toList());

			Long likeCount = feedLikeModuleService.countByFeed(feed);
			Long replyCount = replyModuleService.countByFeedAndStatus(feed);

			boolean isFollowed = followModuleService.isFollow(user, feed.getUser());
			boolean isLiked = feedLikeModuleService.exist(user, feed);

			RestaurantFeedListResp.FeedDTO feedDTO =
				new RestaurantFeedListResp.FeedDTO(feed, feedImageDTOS, likeCount, replyCount);

			RestaurantFeedListResp.RestaurantFeedsDTO restaurantFeedsDTO =
				new RestaurantFeedListResp.RestaurantFeedsDTO(feedDTO, feedRestaurantDTO, isFollowed, isLiked);

			restaurantFeedsDTOList.add(restaurantFeedsDTO);
		}
		return restaurantFeedsDTOList;
	}

	private RecommendedRestaurantResp.RestaurantsDTO createRestaurantsDTO(Restaurant restaurant) {
		List<Feed> feeds = feedModuleService.getTopThree(restaurant);

		List<RecommendedRestaurantResp.FeedsDTO> feedsDTOList = feeds.stream()
			.map(RecommendedRestaurantResp.FeedsDTO::new)
			.collect(Collectors.toList());

		return new RecommendedRestaurantResp.RestaurantsDTO(restaurant, feedsDTOList);
	}
}