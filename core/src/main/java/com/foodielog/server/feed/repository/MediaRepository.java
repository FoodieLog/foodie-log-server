package com.foodielog.server.feed.repository;

import com.foodielog.server.feed.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<Media, Long> {
}
