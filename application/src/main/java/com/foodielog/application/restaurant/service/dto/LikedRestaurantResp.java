package com.foodielog.application.restaurant.service.dto;

import java.util.List;

import org.springframework.lang.Nullable;

import com.foodielog.server.restaurant.entity.Restaurant;

import lombok.Getter;

@Getter
public class LikedRestaurantResp {
	private final List<RestaurantListDTO> content;

	public LikedRestaurantResp(List<RestaurantListDTO> content) {
		this.content = content;
	}

	@Getter
	public static class RestaurantListDTO {
		private final RestaurantDTO restaurant;
		private final IsLikedDTO isLiked;

		public RestaurantListDTO(RestaurantDTO restaurant, IsLikedDTO isLiked) {
			this.restaurant = restaurant;
			this.isLiked = isLiked;
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
			this.category = restaurant.getCategory().getLabel();
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
