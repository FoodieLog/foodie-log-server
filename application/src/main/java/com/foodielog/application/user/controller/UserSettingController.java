package com.foodielog.application.user.controller;

import com.foodielog.application.user.dto.ChangeNotificationDTO;
import com.foodielog.application.user.dto.ChangePasswordDTO;
import com.foodielog.application.user.dto.ChangeProfileDTO;
import com.foodielog.application.user.dto.CheckBadgeApplyDTO;
import com.foodielog.application.user.service.UserSettingService;
import com.foodielog.server._core.security.auth.PrincipalDetails;
import com.foodielog.server._core.util.ApiUtils;
import com.foodielog.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/api/user/setting")
@RestController
public class UserSettingController {
    private final UserSettingService userSettingService;

    @PutMapping("/notification")
    public ResponseEntity<?> changeNotification(
            @RequestBody @Valid ChangeNotificationDTO.Request request,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            Error error
    ) {
        User user = principalDetails.getUser();
        ChangeNotificationDTO.Response response = userSettingService.changeNotification(user, request);
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/badge")
    public ResponseEntity<?> checkBadgeApply(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        CheckBadgeApplyDTO.Response response = userSettingService.checkBadgeApply(user);
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(
            @RequestBody @Valid ChangePasswordDTO.Request request,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            Error error
    ) {
        User user = principalDetails.getUser();
        ChangePasswordDTO.Response response = userSettingService.changePassword(user, request);
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> ChangeProfile(
            @RequestPart(value = "content") @Valid ChangeProfileDTO.Request request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            Errors errors
    ) {
        User user = principalDetails.getUser();
        ChangeProfileDTO.Response response = userSettingService.ChangeProfile(user, request, file);

        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }
}
