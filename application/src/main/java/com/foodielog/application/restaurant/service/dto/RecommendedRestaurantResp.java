package com.foodielog.application.restaurant.service.dto;

import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.restaurant.entity.Restaurant;
import lombok.Getter;

import java.util.List;

@Getter
public class RecommendedRestaurantResp {
    private final List<RestaurantsDTO> restaurantList;

    public RecommendedRestaurantResp(List<RestaurantsDTO> restaurantList) {
        this.restaurantList = restaurantList;
    }

    @Getter
    public static class RestaurantsDTO {
        private final Long restaurantId;
        private final String name;
        private final String roadAddress;
        private final String category;
        private final List<FeedsDTO> feedList;

        public RestaurantsDTO(Restaurant restaurant, List<FeedsDTO> feedList) {
            this.restaurantId = restaurant.getId();
            this.name = restaurant.getName();
            this.roadAddress = restaurant.getRoadAddress();
            this.category = restaurant.getCategory().getLabel();
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
