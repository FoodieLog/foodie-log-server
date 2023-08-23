package com.foodielog.application.restaurant.controller;

import com.foodielog.application.restaurant.dto.LikedRestaurantDTO;
import com.foodielog.application.restaurant.dto.RecommendedRestaurantDTO;
import com.foodielog.application.restaurant.dto.RestaurantFeedListDTO;
import com.foodielog.application.restaurant.service.RestaurantService;
import com.foodielog.server._core.security.auth.PrincipalDetails;
import com.foodielog.server._core.util.ApiUtils;
import com.foodielog.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;

@RequiredArgsConstructor
@Validated
@RequestMapping("/api/restaurant")
@RestController
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping("/map/liked")
    public ResponseEntity<ApiUtils.ApiResult<LikedRestaurantDTO.Response>> getMyRestaurant(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        User user = principalDetails.getUser();
        LikedRestaurantDTO.Response response = restaurantService.getLikedRestaurant(user);
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<ApiUtils.ApiResult<RestaurantFeedListDTO.Response>> getRestaurantDetail(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long restaurantId
    ) {
        User user = principalDetails.getUser();
        RestaurantFeedListDTO.Response response = restaurantService.getRestaurantDetail(user, restaurantId);
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/like")
    public ResponseEntity<ApiUtils.ApiResult<String>> likeRestaurant(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam(name = "restaurant") @Positive Long restaurantId,
            Error error
    ) {
        User user = principalDetails.getUser();
        restaurantService.likeRestaurant(user, restaurantId);
        return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.OK), HttpStatus.OK);
    }

    @DeleteMapping("/unlike")
    public ResponseEntity<HttpStatus> unlikeRestaurant(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam(name = "restaurant") @Positive Long restaurantId,
            Error error
    ) {
        User user = principalDetails.getUser();
        restaurantService.unlikeRestaurant(user, restaurantId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/recommended")
    public ResponseEntity<ApiUtils.ApiResult<RecommendedRestaurantDTO.Response>> getRecommendedRestaurant(
            @RequestParam(name = "address") String address
    ) {
        RecommendedRestaurantDTO.Response response = restaurantService.getRecommendedRestaurant(address);
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }
}
