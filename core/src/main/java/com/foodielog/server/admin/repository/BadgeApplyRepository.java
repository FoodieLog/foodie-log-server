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
            "WHERE (:nickName IS NULL OR b.user.nickName LIKE %:nickName%) " +
            "AND (:status IS NULL OR b.status = :status) ")
    List<BadgeApply> findByStatus(Pageable pageable, @Param("nickName") String nickName, @Param("status") ProcessedStatus status);

    Optional<BadgeApply> findByIdAndStatus(Long badgeApplyId, ProcessedStatus processedStatus);
}
