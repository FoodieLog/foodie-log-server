package com.foodielog.server.admin.repository;

import com.foodielog.server.admin.entity.BlockUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockUserRepository extends JpaRepository<BlockUser, Long> {
}
