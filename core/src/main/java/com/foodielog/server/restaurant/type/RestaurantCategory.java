package com.foodielog.server.restaurant.type;

import java.util.Arrays;

public enum RestaurantCategory {
	KOREAN("한식"),
	CAFE("카페"),
	SNACK("분식"),
	ASIAN("아시아음식"),
	CHINESE("중식"),
	JAPANESE("일식"),
	WESTERN("양식"),
	FUSION("퓨전요리"),
	BAR("술집"),
	ETC("기타");

	private final String label;

	RestaurantCategory(String label) {
		this.label = label;
	}

	public static RestaurantCategory parseCategory(String categoryStr) {
		if (categoryStr == null || categoryStr.isEmpty()) {
			return ETC;
		}

		String[] categories = categoryStr.replaceAll("\\s+", "").split(">");
		if (!categories[0].equals("음식점") || categories.length < 2) {
			return ETC;
		}

		return Arrays.stream(RestaurantCategory.values())
			.filter(category -> category.label.equals(categories[1]))
			.findFirst()
			.orElse(ETC);
	}
}
