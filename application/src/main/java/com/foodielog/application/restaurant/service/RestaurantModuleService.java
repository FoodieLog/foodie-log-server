package com.foodielog.application.restaurant.service;

import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server.restaurant.entity.Restaurant;
import com.foodielog.server.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RestaurantModuleService {
    private final RestaurantRepository restaurantRepository;

    public Restaurant save(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public Optional<Restaurant> getByPlaceId(String placeId) {
        return restaurantRepository.findByKakaoPlaceId(placeId);
    }

    public List<Restaurant> getByAddress(String address) {
        return restaurantRepository.findByRoadAddressContaining(address);
    }

    public Restaurant getById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new Exception404("식당을 찾을 수 없습니다."));
    }
}