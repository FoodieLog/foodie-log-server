package com.foodielog.server.feed.repository;

import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    Long countByUser(User user);

    List<Feed> findByUserId(Long userId);

    @Query("SELECT f FROM Feed f " +
            "WHERE f.user = :user AND f.id > :feedId")
    List<Feed> getFeeds(@Param("user") User user, @Param("feedId") Long feedId, Pageable pageable);

    List<Feed> findAllByRestaurantId(Long restaurantId);
}