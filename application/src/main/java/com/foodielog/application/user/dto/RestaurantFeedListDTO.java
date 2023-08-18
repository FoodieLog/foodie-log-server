package com.foodielog.application.user.dto;

import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.entity.Media;
import com.foodielog.server.restaurant.entity.Restaurant;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;
import java.util.List;

public class RestaurantFeedListDTO {

    @Getter
    public static class Response {
        private final RestaurantInfoDTO restaurantInfo;
        private final List<RestaurantFeedsDTO> content;

        public Response(RestaurantInfoDTO restaurantInfo, List<RestaurantFeedsDTO> content) {
            this.restaurantInfo = restaurantInfo;
            this.content = content;
        }
    }

    @Getter
    public static class RestaurantInfoDTO {
        private final RestaurantDTO restaurantDTO;
        private final IsLikedDTO isLikedDTO;

        public RestaurantInfoDTO(RestaurantDTO restaurantDTO, IsLikedDTO isLikedDTO) {
            this.restaurantDTO = restaurantDTO;
            this.isLikedDTO = isLikedDTO;
        }
    }

    @Getter
    public static class RestaurantFeedsDTO {
        private final FeedDTO feed;
        private final FeedRestaurantDTO restaurant;
        private final boolean isFollowed;
        private final boolean isLiked;

        public RestaurantFeedsDTO(FeedDTO feed, FeedRestaurantDTO restaurant, boolean isFollowed, boolean isLiked) {
            this.feed = feed;
            this.restaurant = restaurant;
            this.isFollowed = isFollowed;
            this.isLiked = isLiked;
        }
    }

    @Getter
    public static class RestaurantDTO {
        private final String name;
        private final String category;
        private final String link;
        private final String roadAddress;
        private final String mapX;
        private final String mapY;

        public RestaurantDTO(Restaurant restaurant) {
            this.name = restaurant.getName();
            this.category = restaurant.getCategory();
            this.link = restaurant.getLink();
            this.roadAddress = restaurant.getRoadAddress();
            this.mapX = restaurant.getMapX();
            this.mapY = restaurant.getMapY();
        }
    }

    @Getter
    public static class IsLikedDTO {

        @Nullable
        private final Long id;

        private final boolean isLiked;

        public IsLikedDTO(Long id, boolean isLiked) {
            this.id = id;
            this.isLiked = isLiked;
        }
    }

    @Getter
    public static class FeedDTO {
        private final Long feedId;
        private final String nickName;
        private final String profileImageUrl;
        private final Timestamp createdAt;
        private final Timestamp updatedAt;
        private final List<FeedImageDTO> feedImages;
        private final String content;
        private final Long likeCount;
        private final Long replyCount;
        private final String share;

        public FeedDTO(Feed feed, List<FeedImageDTO> feedImages, Long likeCount, Long replyCount, String share) {
            this.feedId = feed.getId();
            this.nickName = feed.getUser().getNickName();
            this.profileImageUrl = feed.getUser().getProfileImageUrl();
            this.createdAt = feed.getCreatedAt();
            this.updatedAt = feed.getUpdatedAt();
            this.feedImages = feedImages;
            this.content = feed.getContent();
            this.likeCount = likeCount;
            this.replyCount = replyCount;
            this.share = share;
        }
    }

    @Getter
    public static class FeedImageDTO {
        private final String imageUrl;

        public FeedImageDTO(Media media) {
            this.imageUrl = media.getImageUrl();
        }
    }

    @Getter
    public static class FeedRestaurantDTO {
        private final Long id;
        private final String name;
        private final String category;
        private final String link;
        private final String roadAddress;

        public FeedRestaurantDTO(Restaurant restaurant) {
            this.id = restaurant.getId();
            this.name = restaurant.getName();
            this.category = restaurant.getCategory();
            this.link = restaurant.getLink();
            this.roadAddress = restaurant.getRoadAddress();
        }
    }
}