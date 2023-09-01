package com.foodielog.management.member.controller;

import com.foodielog.management.member.dto.response.BadgeApplyListResp;
import com.foodielog.management.member.dto.response.WithdrawListResp;
import com.foodielog.management.member.service.MemberService;
import com.foodielog.server._core.customValid.valid.ValidEnum;
import com.foodielog.server._core.util.ApiUtils;
import com.foodielog.server.admin.type.ProcessedStatus;
import com.foodielog.server.user.type.Flag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;

@RequiredArgsConstructor
@Validated
@RequestMapping("admin/member")
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/withdraw/list")
    public ResponseEntity<ApiUtils.ApiResult<WithdrawListResp>> withdrawMemberList(
            @RequestParam(required = false) String nickName,
            @RequestParam(required = false) @ValidEnum(enumClass = Flag.class) Flag badge,
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
            @RequestParam(required = false) @ValidEnum(enumClass = ProcessedStatus.class) ProcessedStatus processedStatus,
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
}
