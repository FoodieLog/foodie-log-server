package com.foodielog.server.user.repository;

import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.type.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndStatus(String email, UserStatus status);

    Boolean existsByEmail(String email);

    Boolean existsByNickName(String nickName);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN Follow f ON u = f.followedId " +
            "WHERE u.nickName LIKE %:keyword% AND u.status = 'NORMAL'" +
            "GROUP BY u " +
            "ORDER BY COUNT(f.followingId) DESC")
    List<User> searchUserOrderByFollowedIdDesc(@Param("keyword") String keyword);
}
