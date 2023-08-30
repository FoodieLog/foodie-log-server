package com.foodielog.application.user.controller;

import com.foodielog.application.user.dto.response.*;
import com.foodielog.application.user.service.UserService;
import com.foodielog.server._core.security.auth.PrincipalDetails;
import com.foodielog.server._core.util.ApiUtils;
import com.foodielog.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;

@RequiredArgsConstructor
@Validated
@RequestMapping("/api/user")
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}/profile")
    public ResponseEntity<ApiUtils.ApiResult<UserProfileResp>> getProfile(
            @PathVariable Long userId
    ) {
        UserProfileResp response = userService.getProfile(userId);
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/{userId}/feed/thumbnail")
    public ResponseEntity<ApiUtils.ApiResult<UserThumbnailResp>> getThumbnail(
            @PathVariable Long userId,
            @RequestParam(name = "feed") Long feedId,
            @PageableDefault(size = 15) Pageable pageable
    ) {
        UserThumbnailResp response = userService.getThumbnail(userId, feedId, pageable);
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/{userId}/feed/list")
    public ResponseEntity<ApiUtils.ApiResult<UserFeedListResp>> getFeeds(
            @PathVariable Long userId,
            @RequestParam(name = "feed") Long feedId,
            @PageableDefault(size = 15) Pageable pageable
    ) {
        UserFeedListResp response = userService.getFeeds(userId, feedId, pageable);
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/{userId}/map")
    public ResponseEntity<ApiUtils.ApiResult<UserRestaurantListResp>> getRestaurantListByMap(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long userId
    ) {
        User user = principalDetails.getUser();
        UserRestaurantListResp response = userService.getRestaurantList(userId, user);
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/follow")
    public ResponseEntity<ApiUtils.ApiResult<String>> follow(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam(name = "userId") @Positive Long followedId
    ) {
        User user = principalDetails.getUser();
        userService.follow(user, followedId);
        return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.OK), HttpStatus.OK);
    }

    @DeleteMapping("/unfollow")
    public ResponseEntity<HttpStatus> unFollow(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam(name = "userId") @Positive Long followedId
    ) {
        User user = principalDetails.getUser();
        userService.unFollow(user, followedId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiUtils.ApiResult<UserSearchResp>> search(
            @RequestParam String keyword
    ) {
        UserSearchResp response = userService.search(keyword);
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }
}