package com.foodielog.server.reply.repository;

import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
	Optional<Reply> findByIdAndUserId(Long id, Long userId);
}