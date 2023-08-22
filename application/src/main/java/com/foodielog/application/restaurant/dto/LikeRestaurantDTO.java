package com.foodielog.application.restaurant.dto;

import lombok.Getter;

public class LikeRestaurantDTO {

    @Getter
    public static class Request {
        private Long restaurantId;
    }
}
