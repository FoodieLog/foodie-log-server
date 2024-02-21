package com.foodielog.application.restaurant.controller;

import javax.validation.constraints.Positive;

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

import com.foodielog.application.restaurant.service.RestaurantService;
import com.foodielog.application.restaurant.service.dto.LikedRestaurantResp;
import com.foodielog.application.restaurant.service.dto.RecommendedRestaurantResp;
import com.foodielog.application.restaurant.service.dto.RestaurantFeedListResp;
import com.foodielog.server._core.security.auth.PrincipalDetails;
import com.foodielog.server._core.util.ApiUtils;
import com.foodielog.server.user.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Validated
@RequestMapping("/api/restaurant")
@RestController
public class RestaurantController {

	private final RestaurantService restaurantService;

	@GetMapping("/map/liked")
	public ResponseEntity<ApiUtils.ApiResult<LikedRestaurantResp>> getMyRestaurant(
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		User user = principalDetails.getUser();
		LikedRestaurantResp response = restaurantService.getLikedRestaurant(user);
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
	}

	@GetMapping("/{restaurantId}")
	public ResponseEntity<ApiUtils.ApiResult<RestaurantFeedListResp>> getRestaurantDetail(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@PathVariable Long restaurantId
	) {
		User user = principalDetails.getUser();
		RestaurantFeedListResp response = restaurantService.getRestaurantDetail(user, restaurantId);
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
	}

	@PostMapping("/like")
	public ResponseEntity<ApiUtils.ApiResult<String>> likeRestaurant(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@RequestParam @Positive Long restaurantId
	) {
		User user = principalDetails.getUser();
		restaurantService.likeRestaurant(user, restaurantId);
		return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.CREATED), HttpStatus.CREATED);
	}

	@DeleteMapping("/unlike")
	public ResponseEntity<HttpStatus> unlikeRestaurant(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@RequestParam @Positive Long restaurantId
	) {
		User user = principalDetails.getUser();
		restaurantService.unlikeRestaurant(user, restaurantId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/recommended")
	public ResponseEntity<ApiUtils.ApiResult<RecommendedRestaurantResp>> getRecommendedRestaurant(
		@RequestParam String address
	) {
		RecommendedRestaurantResp response = restaurantService.getRecommendedRestaurant(address);
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
	}
}
