package com.foodielog.application.user.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.foodielog.application.user.controller.dto.ChangeNotificationReq;
import com.foodielog.application.user.controller.dto.ChangePasswordReq;
import com.foodielog.application.user.controller.dto.ChangeProfileReq;
import com.foodielog.application.user.controller.dto.WithdrawReq;
import com.foodielog.application.user.service.dto.ChangeNotificationResp;
import com.foodielog.application.user.service.dto.ChangePasswordResp;
import com.foodielog.application.user.service.dto.ChangeProfileResp;
import com.foodielog.application.user.service.dto.CheckBadgeApplyResp;
import com.foodielog.application.user.service.dto.CreateBadgeApplyResp;
import com.foodielog.application.user.service.dto.LogoutResp;
import com.foodielog.application.user.service.dto.WithdrawResp;
import com.foodielog.application.user.service.UserSettingService;
import com.foodielog.server._core.security.auth.PrincipalDetails;
import com.foodielog.server._core.security.jwt.JwtTokenProvider;
import com.foodielog.server._core.util.ApiUtils;
import com.foodielog.server.user.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/user/setting")
@RestController
public class UserSettingController {
	private final JwtTokenProvider jwtTokenProvider;
	private final UserSettingService userSettingService;

	@PutMapping("/notification")
	public ResponseEntity<ApiUtils.ApiResult<ChangeNotificationResp>> changeNotification(
		@RequestBody @Valid ChangeNotificationReq request,
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		Error error
	) {
		User user = principalDetails.getUser();
		ChangeNotificationResp response = userSettingService.changeNotification(request.toParamWith(user));
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
	}

	@GetMapping("/badge")
	public ResponseEntity<ApiUtils.ApiResult<CheckBadgeApplyResp>> checkBadgeApply(
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		User user = principalDetails.getUser();
		CheckBadgeApplyResp response = userSettingService.checkBadgeApply(user);
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
	}

	@PostMapping("/badge")
	public ResponseEntity<ApiUtils.ApiResult<CreateBadgeApplyResp>> creatBadgeApplyDTO(
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		User user = principalDetails.getUser();
		CreateBadgeApplyResp response = userSettingService.creatBadgeApply(user);
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.CREATED), HttpStatus.CREATED);
	}

	@PutMapping("/password")
	public ResponseEntity<ApiUtils.ApiResult<ChangePasswordResp>> changePassword(
		@RequestBody @Valid ChangePasswordReq request,
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		Error error
	) {
		User user = principalDetails.getUser();
		ChangePasswordResp response = userSettingService.changePassword(request.toParamWith(user));
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiUtils.ApiResult<LogoutResp>> logout(
		@RequestHeader(JwtTokenProvider.HEADER) String header
	) {
		String accessToken = jwtTokenProvider.resolveToken(header);
		LogoutResp response = userSettingService.logout(accessToken);
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
	}

	@PostMapping("/withdraw")
	public ResponseEntity<ApiUtils.ApiResult<WithdrawResp>> withdraw(
		@RequestHeader(JwtTokenProvider.HEADER) String header,
		@RequestBody @Valid WithdrawReq request,
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		Error error
	) {
		String accessToken = jwtTokenProvider.resolveToken(header);
		User user = principalDetails.getUser();
		WithdrawResp response = userSettingService.withdraw(request.toParamWith(accessToken, user));
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.CREATED), HttpStatus.CREATED);
	}

	@PutMapping("/profile")
	public ResponseEntity<ApiUtils.ApiResult<ChangeProfileResp>> ChangeProfile(
		@RequestPart(value = "content") @Valid ChangeProfileReq request,
		@RequestPart(value = "file", required = false) MultipartFile file,
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		Errors errors
	) {
		User user = principalDetails.getUser();
		ChangeProfileResp response = userSettingService.ChangeProfile(request.toParamWith(user, file));
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
	}
}
