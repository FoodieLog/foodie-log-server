package com.foodielog.server.user.repository;

import com.foodielog.server.user.entity.Follow;
import com.foodielog.server.user.entity.FollowPK;
import com.foodielog.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, FollowPK> {
    Long countByFollowedId(User followedId);

    Long countByFollowingId(User followingId);

    boolean existsByFollowedId(User user);

    Optional<Follow> findByFollowingIdAndFollowedId(User following, User followed);
}