package com.foodielog.application.report.service;

import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server.report.entity.Report;
import com.foodielog.server.report.repository.ReportRepository;
import com.foodielog.server.report.type.ReportType;
import com.foodielog.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReportModuleService {
    private ReportRepository reportRepository;

    public Report save(Report report) {
        return reportRepository.save(report);
    }

    public void existsByReporterIdAndTypeAndContentId(User user, ReportType type, Long contentId) {
        boolean isReported = reportRepository.existsByReporterIdAndTypeAndContentId(user, type, contentId);
        if (isReported) {
            throw new Exception404("이미 신고 처리된 컨텐츠입니다.");
        }
    }
}
