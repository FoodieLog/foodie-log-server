package com.foodielog.server.report.repository;

import com.foodielog.server.report.entity.Report;
import com.foodielog.server.report.type.ReportType;
import com.foodielog.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByReporterIdAndTypeAndContentId(User repoterId, ReportType type, Long contentId);
}
