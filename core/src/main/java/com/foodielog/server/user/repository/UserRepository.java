package com.foodielog.server.user.repository;

import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.type.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndStatus(String email, UserStatus status);

    Boolean existsByEmail(String email);

    Boolean existsByNickName(String nickName);
}
