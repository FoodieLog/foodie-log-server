package com.foodielog.management.report.controller;

import com.foodielog.management.report.dto.request.ProcessReq;
import com.foodielog.management.report.service.ReportService;
import com.foodielog.server._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/admin/report")
@Controller
public class ReportController {
    private final ReportService reportService;

    @PatchMapping("/process")
    public ResponseEntity<ApiUtils.ApiResult<String>> process(
            @RequestBody ProcessReq request
    ) {
        reportService.process(request);
        return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.OK), HttpStatus.OK);
    }
}
