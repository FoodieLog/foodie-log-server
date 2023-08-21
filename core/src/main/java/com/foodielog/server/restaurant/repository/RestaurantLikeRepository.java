package com.foodielog.server.restaurant.repository;

import com.foodielog.server.restaurant.entity.Restaurant;
import com.foodielog.server.restaurant.entity.RestaurantLike;
import com.foodielog.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantLikeRepository extends JpaRepository<RestaurantLike, Long> {
    boolean existsByUser(User user);

    RestaurantLike findByUserIdAndRestaurantId(Long userId, Long restaurantId);

    boolean existsByUserAndRestaurant(User user, Restaurant restaurant);

    List<RestaurantLike> findByUser(User user);
}