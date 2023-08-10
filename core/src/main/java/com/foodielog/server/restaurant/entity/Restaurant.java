package com.foodielog.server.restaurant.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "restaurant_tb")
@Entity
public class Restaurant {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private String category;

	@Column(nullable = false)
	private String link;

	@Column(nullable = false)
	private String mapX;

	@Column(nullable = false)
	private String mapY;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false)
	private String roadAddress;

	public static Restaurant createRestaurant(String name, String category, String link, String mapX, String mapY
			, String address, String roadAddress) {
		Restaurant restaurant = new Restaurant();
		restaurant.name = name;
		restaurant.category = category;
		restaurant.link = link;
		restaurant.mapX = mapX;
		restaurant.mapY = mapY;
		restaurant.address = address;
		restaurant.roadAddress = roadAddress;
		return restaurant;
	}
}
