package com.foodielog.server.restaurant.repository;

import com.foodielog.server.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findByKakaoPlaceId(String kakaoPlaceId);

    @Query("SELECT r, COUNT(rl) AS likeCount " +
            "FROM Restaurant r " +
            "LEFT JOIN RestaurantLike rl ON r.id = rl.restaurant.id " +
            "WHERE r.roadAddress LIKE %:address% " +
            "GROUP BY r.id " +
            "ORDER BY likeCount DESC")
    List<Restaurant> findByRoadAddressContaining(@Param("address") String address);
}
