package com.foodielog.application.user.controller;

import com.foodielog.application.user.dto.ChangePasswordDTO;
import com.foodielog.application.user.service.UserSettingService;
import com.foodielog.server._core.security.auth.PrincipalDetails;
import com.foodielog.server._core.util.ApiUtils;
import com.foodielog.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/api/user/setting")
@RestController
public class UserSettingController {
    private final UserSettingService userSettingService;

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
}
