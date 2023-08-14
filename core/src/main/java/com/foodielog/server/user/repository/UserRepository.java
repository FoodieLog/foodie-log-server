package com.foodielog.server.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodielog.server.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	Boolean existsByEmail(String email);
}
