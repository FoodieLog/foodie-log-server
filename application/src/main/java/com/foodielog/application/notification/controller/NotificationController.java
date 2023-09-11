package com.foodielog.application.notification.controller;

import com.foodielog.application.notification.dto.response.NotificationListResp;
import com.foodielog.application.notification.service.NotificationService;
import com.foodielog.server._core.security.auth.PrincipalDetails;
import com.foodielog.server._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Validated
@RequestMapping("/api/notification")
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/list")
    public ResponseEntity<ApiUtils.ApiResult<NotificationListResp>> notificationList(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        NotificationListResp response = notificationService.getNotificationList(principalDetails.getUser());
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }
}