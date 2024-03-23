package com.foodielog.server.mention.repository;

import com.foodielog.server.mention.entity.Mention;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentionRepository extends JpaRepository<Mention, Long> {
}
