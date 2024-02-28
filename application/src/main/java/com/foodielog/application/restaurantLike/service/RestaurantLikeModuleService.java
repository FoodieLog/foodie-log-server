package com.foodielog.application.restaurantLike.service;

import com.foodielog.server.restaurant.entity.Restaurant;
import com.foodielog.server.restaurant.entity.RestaurantLike;
import com.foodielog.server.restaurant.repository.RestaurantLikeRepository;
import com.foodielog.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RestaurantLikeModuleService {
    private final RestaurantLikeRepository restaurantLikeRepository;

    public RestaurantLike save(RestaurantLike restaurantLike) {
        return restaurantLikeRepository.save(restaurantLike);
    }

    public boolean exist(User user, Restaurant restaurant) {
        return restaurantLikeRepository.existsByUserAndRestaurant(user, restaurant);
    }

    public void delete(RestaurantLike restaurantLike) {
        restaurantLikeRepository.delete(restaurantLike);
    }

    public List<RestaurantLike> getRestaurantLikes(User user) {
        return restaurantLikeRepository.findByUser(user);
    }

    public RestaurantLike get(User user, Restaurant restaurant) {
        return restaurantLikeRepository.findByUserIdAndRestaurantId(user.getId(), restaurant.getId());
    }
}