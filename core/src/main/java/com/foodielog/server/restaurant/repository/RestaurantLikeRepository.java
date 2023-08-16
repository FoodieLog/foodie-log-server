package com.foodielog.server.restaurant.repository;

import com.foodielog.server.restaurant.entity.RestaurantLike;
import com.foodielog.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantLikeRepository extends JpaRepository<RestaurantLike, Long> {
    boolean existsByUser(User user);
}