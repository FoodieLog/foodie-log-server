package com.foodielog.server.feed.repository;

import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Long> {
    List<Media> findByFeed(Feed feed);
}
