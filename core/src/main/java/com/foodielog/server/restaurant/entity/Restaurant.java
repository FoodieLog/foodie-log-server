package com.foodielog.server.restaurant.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.foodielog.server.restaurant.type.RestaurantCategory;

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
    private String kakaoPlaceId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RestaurantCategory category;

    @Column(nullable = false)
    private String kakaoCategory;

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

    public static Restaurant createRestaurant(String name, String kakaoPlaceId, String phone, RestaurantCategory category,
                                              String kakaoCategory, String link, String mapX, String mapY, String address, String roadAddress) {
        Restaurant restaurant = new Restaurant();
        restaurant.name = name;
        restaurant.kakaoPlaceId = kakaoPlaceId;
        restaurant.phone = phone;
        restaurant.category = category;
        restaurant.kakaoCategory = kakaoCategory;
        restaurant.link = link;
        restaurant.mapX = mapX;
        restaurant.mapY = mapY;
        restaurant.address = address;
        restaurant.roadAddress = roadAddress;
        return restaurant;
    }
}
