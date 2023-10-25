package com.foodielog.server.user.repository;

import com.foodielog.server.user.entity.Follow;
import com.foodielog.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    @Query("SELECT COUNT(f) " +
            "FROM Follow f " +
            "LEFT JOIN User u ON f.followingId = u " +
            "WHERE f.followedId = :followedId AND u.status = 'NORMAL'")
    Long countByFollowedId(User followedId);

    @Query("SELECT COUNT(f) " +
            "FROM Follow f " +
            "LEFT JOIN User u ON f.followedId = u " +
            "WHERE f.followingId = :followingId AND u.status = 'NORMAL'")
    Long countByFollowingId(User followingId);

    Optional<Follow> findByFollowingIdAndFollowedId(User following, User followed);

    boolean existsByFollowingIdAndFollowedId(User following, User followed);

    @Query("SELECT f " +
            "FROM Follow f " +
            "LEFT JOIN User u ON f.followingId = u " +
            "WHERE f.followedId = :owner AND u.status = 'NORMAL'")
    List<Follow> findByFollowedId(User owner);

    @Query("SELECT f " +
            "FROM Follow f " +
            "LEFT JOIN User u ON f.followedId = u " +
            "WHERE f.followingId = :owner AND u.status = 'NORMAL'")
    List<Follow> findByFollowingId(User owner);
}