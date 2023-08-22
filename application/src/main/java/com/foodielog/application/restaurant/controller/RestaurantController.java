package com.foodielog.application.restaurant.controller;

import com.foodielog.application.restaurant.dto.LikedRestaurantDTO;
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
    public ResponseEntity<?> getMyRestaurant(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        User user = principalDetails.getUser();
        LikedRestaurantDTO.Response response = restaurantService.getLikedRestaurant(user);
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<?> getRestaurantDetail(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long restaurantId
    ) {
        User user = principalDetails.getUser();
        RestaurantFeedListDTO.Response response = restaurantService.getRestaurantDetail(user, restaurantId);
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/like")
    public ResponseEntity<?> likeRestaurant(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam(name = "restaurant") @Positive Long restaurantId,
            Error error
    ) {
        User user = principalDetails.getUser();
        restaurantService.likeRestaurant(user, restaurantId);
        return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.OK), HttpStatus.OK);
    }

    @DeleteMapping("/unlike")
    public ResponseEntity<?> unlikeRestaurant(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam(name = "restaurant") @Positive Long restaurantId,
            Error error
    ) {
        User user = principalDetails.getUser();
        restaurantService.unlikeRestaurant(user, restaurantId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
