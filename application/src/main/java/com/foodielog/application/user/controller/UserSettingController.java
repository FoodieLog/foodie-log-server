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

import com.foodielog.application.user.dto.request.ChangeNotificationParam;
import com.foodielog.application.user.dto.request.ChangeNotificationReq;
import com.foodielog.application.user.dto.request.ChangePasswordParam;
import com.foodielog.application.user.dto.request.ChangePasswordReq;
import com.foodielog.application.user.dto.request.ChangeProfileParam;
import com.foodielog.application.user.dto.request.ChangeProfileReq;
import com.foodielog.application.user.dto.request.WithdrawParam;
import com.foodielog.application.user.dto.request.WithdrawReq;
import com.foodielog.application.user.dto.response.ChangeNotificationResp;
import com.foodielog.application.user.dto.response.ChangePasswordResp;
import com.foodielog.application.user.dto.response.ChangeProfileResp;
import com.foodielog.application.user.dto.response.CheckBadgeApplyResp;
import com.foodielog.application.user.dto.response.CreateBadgeApplyResp;
import com.foodielog.application.user.dto.response.LogoutResp;
import com.foodielog.application.user.dto.response.WithdrawResp;
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
		ChangeNotificationParam parameter = ChangeNotificationParam.builder()
			.user(principalDetails.getUser())
			.flag(request.getFlag())
			.build();
		ChangeNotificationResp response = userSettingService.changeNotification(parameter);
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
		ChangePasswordParam parameter = ChangePasswordParam.builder().
			user(principalDetails.getUser())
			.oldPassword(request.getOldPassword())
			.newPassword(request.getNewPassword())
			.build();
		ChangePasswordResp response = userSettingService.changePassword(parameter);
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiUtils.ApiResult<LogoutResp>> logout(
		@RequestHeader(JwtTokenProvider.HEADER) String accessToken
	) {
		accessToken = jwtTokenProvider.resolveToken(accessToken);
		LogoutResp response = userSettingService.logout(accessToken);
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
	}

	@PostMapping("/withdraw")
	public ResponseEntity<ApiUtils.ApiResult<WithdrawResp>> withdraw(
		@RequestHeader(JwtTokenProvider.HEADER) String accessToken,
		@RequestBody @Valid WithdrawReq request,
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		Error error
	) {
		WithdrawParam parameter = WithdrawParam.builder()
			.accessToken(jwtTokenProvider.resolveToken(accessToken))
			.user(principalDetails.getUser())
			.withdrawReason(request.getWithdrawReason())
			.build();
		WithdrawResp response = userSettingService.withdraw(parameter);
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.CREATED), HttpStatus.CREATED);
	}

	@PutMapping("/profile")
	public ResponseEntity<ApiUtils.ApiResult<ChangeProfileResp>> ChangeProfile(
		@RequestPart(value = "content") @Valid ChangeProfileReq request,
		@RequestPart(value = "file", required = false) MultipartFile file,
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		Errors errors
	) {
		ChangeProfileParam parameter = ChangeProfileParam.builder()
			.user(principalDetails.getUser())
			.nickName(request.getNickName())
			.aboutMe(request.getAboutMe())
			.file(file)
			.build();
		ChangeProfileResp response = userSettingService.ChangeProfile(parameter);
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
	}
}
