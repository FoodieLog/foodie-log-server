package com.foodielog.server.reply.repository;

import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.type.ContentStatus;
import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Optional<Reply> findByIdAndStatus(Long id, ContentStatus status);

    Optional<Reply> findByIdAndUserIdAndStatus(Long id, Long userId, ContentStatus status);

    List<Reply> findByUserId(Long id);

    @Query("SELECT r FROM Reply r " +
            "JOIN FETCH r.user " +
            "WHERE r.feed.id = :feedId and r.id > :id and r.status = 'NORMAL'")
    List<Reply> getReplyList(@Param("feedId") Long feedId, @Param("id") Long id, Pageable pageable);

    Long countByFeedAndStatus(Feed feed, ContentStatus status);

    Long countByUserAndStatus(User user, ContentStatus status);

    List<Reply> findByFeedIdAndStatus(Long feedId, ContentStatus contentStatus);

    List<Reply> findByUserIdAndStatus(Long userId, ContentStatus contentStatus);
}