package com.foodielog.management.report.controller;

import com.foodielog.management.report.controller.dto.ProcessReq;
import com.foodielog.management.report.service.ReportService;
import com.foodielog.management.report.service.dto.ReportListResp;
import com.foodielog.server._core.customValid.valid.ValidEnum;
import com.foodielog.server._core.util.ApiUtils;
import com.foodielog.server.feed.type.ContentStatus;
import com.foodielog.server.report.type.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/report")
@RestController
public class ReportController {
    private final ReportService reportService;

    @PatchMapping("/process")
    public ResponseEntity<ApiUtils.ApiResult<String>> process(
            @RequestBody ProcessReq request
    ) {
        reportService.process(request.toParam());
        return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<ApiUtils.ApiResult<ReportListResp>> reportList(
            @RequestParam(required = false) String nickName,
            @RequestParam(required = false) @ValidEnum(enumClass = ReportType.class, nullable = true) ReportType type,
            @RequestParam(required = false) @ValidEnum(enumClass = ContentStatus.class, nullable = true) ContentStatus status,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        ReportListResp response = reportService.getReportList(nickName, type, status, pageable);
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }
}
