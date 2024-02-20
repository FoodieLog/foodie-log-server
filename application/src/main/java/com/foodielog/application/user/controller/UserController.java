package com.foodielog.application.user.controller;

import javax.validation.constraints.Positive;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodielog.application.user.service.dto.FollowListResp;
import com.foodielog.application.user.service.dto.FollowerListResp;
import com.foodielog.application.user.service.dto.UserFeedResp;
import com.foodielog.application.user.service.dto.UserProfileResp;
import com.foodielog.application.user.service.dto.UserRestaurantListResp;
import com.foodielog.application.user.service.dto.UserSearchResp;
import com.foodielog.application.user.service.UserService;
import com.foodielog.server._core.security.auth.PrincipalDetails;
import com.foodielog.server._core.util.ApiUtils;
import com.foodielog.server.user.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Validated
@RequestMapping("/api/user")
@RestController
public class UserController {

	private final UserService userService;

	@GetMapping("/{userId}/profile")
	public ResponseEntity<ApiUtils.ApiResult<UserProfileResp>> getProfile(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@PathVariable Long userId
	) {
		User user = principalDetails.getUser();
		UserProfileResp response = userService.getProfile(user, userId);
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
	}

	@GetMapping("/{userId}/feed")
	public ResponseEntity<ApiUtils.ApiResult<UserFeedResp>> getFeeds(
		@PathVariable Long userId,
		@RequestParam(required = false) @Positive Long feedId,
		@PageableDefault(size = 15, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
	) {
		UserFeedResp response = userService.getFeeds(userId, feedId, pageable);
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
		@RequestParam @Positive Long followedId
	) {
		User user = principalDetails.getUser();
		userService.follow(user, followedId);
		return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.CREATED), HttpStatus.CREATED);
	}

	@DeleteMapping("/unfollow")
	public ResponseEntity<HttpStatus> unFollow(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@RequestParam @Positive Long followedId
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

	@GetMapping("/follower/list")
	public ResponseEntity<ApiUtils.ApiResult<FollowerListResp>> followerList(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@RequestParam @Positive Long userId
	) {
		User user = principalDetails.getUser();
		FollowerListResp response = userService.getFollower(user, userId);
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
	}

	@GetMapping("/follow/list")
	public ResponseEntity<ApiUtils.ApiResult<FollowListResp>> followList(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@RequestParam @Positive Long userId
	) {
		User user = principalDetails.getUser();
		FollowListResp response = userService.getFollow(user, userId);
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
	}
}