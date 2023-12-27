package com.foodielog.server.feed.repository;

import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.entity.FeedLike;
import com.foodielog.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {
    boolean existsByUserAndFeed(User user, Feed feed);

    Optional<FeedLike> findByUserAndFeed(User user, Feed feed);

    Long countByFeed(Feed feed);

    Optional<FeedLike> findByUserId(Long id);
}