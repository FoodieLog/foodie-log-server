package com.foodielog.server.user.repository;

import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.type.Flag;
import com.foodielog.server.user.type.Role;
import com.foodielog.server.user.type.UserStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndStatus(String email, UserStatus status);

    Optional<User> findByEmailAndRole(String email, Role role);

    Optional<User> findByIdAndStatus(Long userId, UserStatus status);

    Boolean existsByEmail(String email);

    Boolean existsByNickName(String nickName);

    @Query("SELECT u FROM User u " +
        "LEFT JOIN Follow f ON u = f.followedId " +
        "WHERE u.nickName LIKE %:keyword% AND u.status = 'NORMAL'" +
        "GROUP BY u " +
        "ORDER BY COUNT(f.followingId) DESC")
    List<User> searchUserOrderByFollowedIdDesc(@Param("keyword") String keyword);

    @Query("SELECT u FROM User u " +
        "WHERE (:nickName IS NULL OR u.nickName LIKE %:nickName%) " +
        "AND (:badgeFlag IS NULL OR u.badgeFlag = :badgeFlag) " +
        "AND ((:status IS NULL AND (u.status = 'NORMAL' OR u.status = 'BLOCK')) OR u.status = :status) ")
    List<User> findAllByFlagAndStatus(@Param("nickName") String nickName,
        @Param("badgeFlag") Flag badgeFlag,
        @Param("status") UserStatus status, Pageable pageable);

    UserStatus findStatusByEmail(String email);
}
