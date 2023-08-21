package com.foodielog.application.restaurant.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

public class LikeRestaurantDTO {

    @Getter
    public static class Request {

        @NotBlank
        private Long restaurantId;
    }
}
