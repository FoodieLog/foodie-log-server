package com.foodielog.server.admin.repository;

import com.foodielog.server.admin.entity.BadgeApply;
import com.foodielog.server.admin.type.ProcessedStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BadgeApplyRepository extends JpaRepository<BadgeApply, Long> {

    Optional<BadgeApply> findByUserId(Long id);

    @Query("SELECT b FROM BadgeApply b " +
            "WHERE (b.user.nickName LIKE %:nickName% OR :nickName IS NULL) " +
            "AND (b.status = :status OR :status IS NULL) ")
    List<BadgeApply> findByStatus(Pageable pageable, @Param("nickName") String nickName, @Param("status") ProcessedStatus status);

    Optional<BadgeApply> findByIdAndStatus(Long badgeApplyId, ProcessedStatus processedStatus);
}
