package com.foodielog.application.restaurant.dto;

import lombok.Getter;

import javax.validation.constraints.Positive;

public class LikeRestaurantDTO {

    @Getter
    public static class Request {

        @Positive(message = "해당 맛집이 존재하지 않습니다.")
        private Long restaurantId;
    }
}
