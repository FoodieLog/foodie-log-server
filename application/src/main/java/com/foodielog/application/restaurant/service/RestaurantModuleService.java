package com.foodielog.application.restaurant.service;

import com.foodielog.server.restaurant.entity.Restaurant;
import com.foodielog.server.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RestaurantModuleService {
    private final RestaurantRepository restaurantRepository;

    public Restaurant save(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public Optional<Restaurant> getRestaurantByPlaceId(String id) {
        return restaurantRepository.findByKakaoPlaceId(id);
    }
}
