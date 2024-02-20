package com.foodielog.application.notification.controller;

import com.foodielog.application.notification.dto.NotificationTokenReq;
import com.foodielog.application.notification.service.dto.NotificationListResp;
import com.foodielog.application.notification.service.NotificationService;
import com.foodielog.server._core.security.auth.PrincipalDetails;
import com.foodielog.server._core.util.ApiUtils;
import com.foodielog.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @PostMapping("/push")
    public ResponseEntity<?> notificationPush(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid NotificationTokenReq request
    ) {
        User user = principalDetails.getUser();
        notificationService.registerFcmToken(request.toParamWith(user));
        return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.CREATED), HttpStatus.CREATED);
    }
}