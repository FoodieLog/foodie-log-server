package com.foodielog.server.report.repository;

import com.foodielog.server.admin.type.ProcessedStatus;
import com.foodielog.server.report.entity.Report;
import com.foodielog.server.report.type.ReportType;
import com.foodielog.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByReporterIdAndTypeAndContentId(User reporterId, ReportType type, Long contentId);

    @Query("SELECT COUNT(DISTINCT r.contentId) FROM Report r " +
            "WHERE r.reportedId = :reportedId AND r.status = :status")
    long countProcessedByStatus(@Param("reportedId") User reportedId, @Param("status") ProcessedStatus status);

    Optional<List<Report>> findAllByReportedIdAndContentId(User reportedId, Long contentId);
}
