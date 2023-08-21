package com.foodielog.server.admin.repository;

import com.foodielog.server.admin.entity.BadgeApply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BadgeApplyRepository extends JpaRepository<BadgeApply, Long> {

    Optional<BadgeApply> findByUserId(Long id);

}
