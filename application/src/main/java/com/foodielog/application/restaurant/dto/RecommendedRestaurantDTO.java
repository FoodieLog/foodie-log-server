package com.foodielog.application.restaurant.dto;

import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.restaurant.entity.Restaurant;
import lombok.Getter;

import java.util.List;

public class RecommendedRestaurantDTO {

    @Getter
    public static class Response {
        private final List<RestaurantsDTO> restaurantList;

        public Response(List<RestaurantsDTO> restaurantList) {
            this.restaurantList = restaurantList;
        }
    }

    @Getter
    public static class RestaurantsDTO {
        private final Long restaurantId;
        private final String name;
        private final String roadAddress;
        private final List<FeedsDTO> feedList;

        public RestaurantsDTO(Restaurant restaurant, List<FeedsDTO> feedList) {
            this.restaurantId = restaurant.getId();
            this.name = restaurant.getName();
            this.roadAddress = restaurant.getRoadAddress();
            this.feedList = feedList;
        }
    }

    @Getter
    public static class FeedsDTO {
        private Long feedId;
        private String thumbnailUrl;

        public FeedsDTO(Feed feed) {
            this.feedId = feed.getId();
            this.thumbnailUrl = feed.getThumbnailUrl();
        }
    }
}
