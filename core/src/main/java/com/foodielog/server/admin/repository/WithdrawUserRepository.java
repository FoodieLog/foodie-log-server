package com.foodielog.server.admin.repository;

import com.foodielog.server.admin.entity.WithdrawUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawUserRepository extends JpaRepository<WithdrawUser, Long> {
}
