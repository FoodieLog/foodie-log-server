package com.foodielog.server.user.repository;

import com.foodielog.server.user.entity.Follow;
import com.foodielog.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Long countByFollowedId(User followedId);

    Long countByFollowingId(User followingId);

    Optional<Follow> findByFollowingIdAndFollowedId(User following, User followed);

    boolean existsByFollowingIdAndFollowedId(User following, User followed);

    List<Follow> findByFollowedId(User owner);

    List<Follow> findByFollowingId(User owner);
}