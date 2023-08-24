package com.foodielog.server.feed.repository;

import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.type.ContentStatus;
import com.foodielog.server.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    Long countByUser(User user);

    List<Feed> findByUserId(Long userId);

    List<Feed> findByUserIdAndStatus(Long userId, ContentStatus status);

    @Query("SELECT f FROM Feed f " +
            "WHERE f.user = :user AND f.id > :feedId")
    List<Feed> getFeeds(@Param("user") User user, @Param("feedId") Long feedId, Pageable pageable);

    List<Feed> findAllByRestaurantIdAndStatus(Long restaurantId, ContentStatus status);

    @Query("SELECT f, COUNT(fl) AS likeCount " +
            "FROM Feed f " +
            "LEFT JOIN FeedLike fl ON f.id = fl.feed.id " +
            "WHERE f.restaurant.id = :restaurantId " +
            "AND f.status = 'NORMAL' " +
            "GROUP BY f.id " +
            "ORDER BY likeCount DESC, f.id DESC")
    List<Feed> findTop3ByRestaurantId(Long restaurantId, Pageable pageable);
}