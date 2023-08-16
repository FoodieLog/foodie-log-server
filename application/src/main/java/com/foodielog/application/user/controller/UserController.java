package com.foodielog.application.user.controller;

import com.foodielog.application.user.dto.response.UserProfileDTO;
import com.foodielog.application.user.dto.response.UserThumbnailDTO;
import com.foodielog.application.user.service.UserService;
import com.foodielog.server._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}/profile")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        UserProfileDTO.Response response = userService.getProfile(userId);
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/{userId}/feed/thumbnail")
    public ResponseEntity<?> getThumbnail(@PathVariable Long userId,
                                          @RequestParam(name = "id") Long feedId,
                                          @PageableDefault Pageable pageable) {
        UserThumbnailDTO.Response response = userService.getThumbnail(userId, feedId, pageable);
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/{userId}/feed/list")
    public ResponseEntity<?> getFeeds(@PathVariable String userId,
                                      @RequestParam Long feedId) {
        userService.getFeeds(userId, feedId);
        return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.OK), HttpStatus.OK);
    }
}
