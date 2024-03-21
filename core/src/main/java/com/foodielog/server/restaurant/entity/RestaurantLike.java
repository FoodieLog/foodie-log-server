package com.foodielog.server.restaurant.entity;

import com.foodielog.server.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "restaurant_like_tb")
@Entity
public class RestaurantLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    private Timestamp createdAt;

    public static RestaurantLike createRestaurantLike(Restaurant restaurant, User user) {
        RestaurantLike restaurantLike = new RestaurantLike();
        restaurantLike.restaurant = restaurant;
        restaurantLike.user = user;
        return restaurantLike;
    }
}
