package com.foodielog.server.report.repository;

import com.foodielog.server.admin.type.ProcessedStatus;
import com.foodielog.server.feed.type.ContentStatus;
import com.foodielog.server.report.entity.Report;
import com.foodielog.server.report.type.ReportType;
import com.foodielog.server.user.entity.User;
import org.springframework.data.domain.Pageable;
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

    @Query(value = "SELECT r.* FROM report_tb r " +
            "INNER JOIN (SELECT r2.content_id, MIN(r2.created_at) AS min_created_at " +
            "FROM report_tb r2 " +
            "LEFT JOIN user_tb reporter ON r2.reporter_id = reporter.id " +
            "LEFT JOIN user_tb reported ON r2.reported_id = reported.id " +
            "WHERE (:nickName IS NULL OR reporter.nick_name LIKE %:nickName% OR reported.nick_name LIKE %:nickName%) " +
            "AND (:type IS NULL OR r2.type = :type) AND (:status IS NULL OR r2.status = :status) " +
            "GROUP BY r2.content_id) AS grouped_reports " +
            "ON r.content_id = grouped_reports.content_id " +
            "ORDER BY grouped_reports.min_created_at, r.content_id, r.created_at", nativeQuery = true)
    List<Report> findAllByParam(@Param("nickName") String nickName, @Param("type") ReportType type,
                                @Param("status") ContentStatus status, Pageable pageable);
}
