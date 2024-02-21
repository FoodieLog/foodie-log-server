package com.foodielog.management.member.controller;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodielog.management.member.controller.dto.BlockReq;
import com.foodielog.management.member.service.MemberService;
import com.foodielog.management.member.service.dto.BadgeApplyListResp;
import com.foodielog.management.member.service.dto.MemberListResp;
import com.foodielog.management.member.service.dto.WithdrawListResp;
import com.foodielog.server._core.customValid.valid.ValidEnum;
import com.foodielog.server._core.util.ApiUtils;
import com.foodielog.server.admin.type.ProcessedStatus;
import com.foodielog.server.user.type.Flag;
import com.foodielog.server.user.type.UserStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Validated
@RequestMapping("admin/member")
@RestController
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/withdraw/list")
	public ResponseEntity<ApiUtils.ApiResult<WithdrawListResp>> withdrawMemberList(
		@RequestParam(required = false) String nickName,
		@RequestParam(required = false) @ValidEnum(enumClass = Flag.class, nullable = true) Flag badge,
		@PageableDefault(size = 20) Pageable pageable
	) {
		WithdrawListResp response = memberService.getWithdrawList(nickName, badge, pageable);
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
	}

	@PatchMapping("/withdraw/restore")
	public ResponseEntity<ApiUtils.ApiResult<String>> restoreMember(
		@RequestParam Long withdrawId
	) {
		memberService.restoreMember(withdrawId);
		return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.OK), HttpStatus.OK);
	}

	@GetMapping("/badge/list")
	public ResponseEntity<ApiUtils.ApiResult<BadgeApplyListResp>> badgeApplyList(
		@RequestParam(required = false) String nickName,
		@RequestParam(required = false) @ValidEnum(enumClass = ProcessedStatus.class, nullable = true) ProcessedStatus processedStatus,
		@PageableDefault(size = 20) Pageable pageable
	) {
		BadgeApplyListResp response = memberService.getBadgeApplyList(nickName, processedStatus, pageable);
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
	}

	@PatchMapping("badge/{process}")
	public ResponseEntity<ApiUtils.ApiResult<String>> badgeProcessed(
		@RequestParam @Positive Long badgeApplyId,
		@PathVariable String process
	) {
		memberService.badgeProcessed(badgeApplyId, process);
		return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.OK), HttpStatus.OK);
	}

	@GetMapping("/list")
	public ResponseEntity<ApiUtils.ApiResult<MemberListResp>> memberList(
		@RequestParam(required = false) String nickName,
		@RequestParam(required = false) @ValidEnum(enumClass = Flag.class, nullable = true) Flag badge,
		@RequestParam(required = false) @ValidEnum(enumClass = UserStatus.class, nullable = true) UserStatus userStatus,
		@PageableDefault(size = 20) Pageable pageable
	) {
		MemberListResp response = memberService.getMemberList(nickName, badge, userStatus, pageable);
		return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
	}

	@PostMapping("/block")
	public ResponseEntity<ApiUtils.ApiResult<String>> block(
		@RequestBody @Valid BlockReq request
	) {
		memberService.blockProcessed(request.toParam());
		return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.CREATED), HttpStatus.CREATED);
	}
}
