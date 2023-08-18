package com.foodielog.application.user.dto;

import com.foodielog.server.restaurant.entity.Restaurant;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.List;

public class UserRestaurantListDTO {

    @Getter
    public static class Response {
        private final List<RestaurantListDTO> content;

        public Response(List<RestaurantListDTO> content) {
            this.content = content;
        }

        @Getter
        public static class RestaurantListDTO {
            private final RestaurantDTO restaurant;
            private final IsLikedDTO isLikedDTO;

            public RestaurantListDTO(RestaurantDTO restaurant, IsLikedDTO isLikedDTO) {
                this.restaurant = restaurant;
                this.isLikedDTO = isLikedDTO;
            }
        }

        @Getter
        public static class RestaurantDTO {
            private final Long id;
            private final String name;
            private final String category;
            private final String link;
            private final String roadAddress;
            private final String mapX;
            private final String mapY;

            public RestaurantDTO(Restaurant restaurant) {
                this.id = restaurant.getId();
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
    }
}