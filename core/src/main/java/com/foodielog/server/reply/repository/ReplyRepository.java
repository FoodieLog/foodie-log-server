package com.foodielog.server.reply.repository;

import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.type.ContentStatus;
import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    Optional<Reply> findByIdAndStatus(Long id, ContentStatus status);

    Optional<Reply> findByIdAndUserIdAndStatus(Long id, Long userId, ContentStatus status);

    List<Reply> findByUserId(Long id);

    @Query("SELECT r FROM Reply r " +
        "JOIN FETCH r.user " +
        "LEFT JOIN FETCH r.children c " +
        "LEFT JOIN FETCH c.user cu " +
        "WHERE r.feed.id = :feedId AND r.parent = NULL AND r.status = 'NORMAL' AND r.id > :last " +
        "ORDER BY r.id ASC, c.id ASC")
    List<Reply> getReplyList(@Param("feedId") Long feedId, @Param("last") Long last,
        Pageable pageable);

    Long countByFeedAndStatus(Feed feed, ContentStatus status);

    Long countByUserAndStatus(User user, ContentStatus status);

    List<Reply> findByFeedIdAndStatus(Long feedId, ContentStatus contentStatus);

    List<Reply> findByUserIdAndStatus(Long userId, ContentStatus contentStatus);
}