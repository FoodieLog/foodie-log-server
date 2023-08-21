package com.foodielog.application.restaurant.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

public class UnlikeRestaurantDTO {

    @Getter
    public static class Request {

        @NotBlank
        private Long restaurantId;
    }
}
