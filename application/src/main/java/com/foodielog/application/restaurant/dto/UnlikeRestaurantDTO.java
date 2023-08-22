package com.foodielog.application.restaurant.dto;

import lombok.Getter;

public class UnlikeRestaurantDTO {

    @Getter
    public static class Request {
        private Long restaurantId;
    }
}
