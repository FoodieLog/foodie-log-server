package com.foodielog.server.reply.repository;

import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
	Optional<Reply> findByIdAndUserId(Long id, Long userId);

	@Query("SELECT r FROM Reply r " +
			"JOIN FETCH r.user " +
			"WHERE r.feed.id = :feedId and r.id > :id")
	List<Reply> getReplyList(@Param("feedId") Long feedId, @Param("id") Long id, Pageable pageable);

	Long countByFeed(Feed feed);
}