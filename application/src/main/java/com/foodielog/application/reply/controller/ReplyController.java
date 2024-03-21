package com.foodielog.application.reply.controller;

import com.foodielog.application.reply.dto.ReplyCreateReq;
import com.foodielog.application.reply.dto.ReportReplyReq;
import com.foodielog.application.reply.service.ReplyService;
import com.foodielog.application.reply.service.dto.ReplyCreateResp;
import com.foodielog.application.reply.service.dto.ReplyListResp;
import com.foodielog.server._core.security.auth.PrincipalDetails;
import com.foodielog.server._core.util.ApiUtils;
import com.foodielog.server.user.entity.User;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/reply")
@RestController
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/{feedId}")
    public ResponseEntity<ApiUtils.ApiResult<ReplyCreateResp>> saveReply(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable Long feedId,
        @Valid @RequestBody ReplyCreateReq request,
        Errors errors
    ) {
        User user = principalDetails.getUser();
        ReplyCreateResp response = replyService.createReply(request.toParamWith(user, feedId));
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.CREATED),
            HttpStatus.CREATED);
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<HttpStatus> deleteReply(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable Long replyId
    ) {
        replyService.deleteReply(principalDetails.getUser(), replyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{feedId}")
    public ResponseEntity<ApiUtils.ApiResult<ReplyListResp.ListDTO>> getReplyList(
        @PathVariable Long feedId,
        @RequestParam Long last,
        @PageableDefault(size = 15) Pageable pageable
    ) {
        ReplyListResp.ListDTO response = replyService.getReplys(feedId, last, pageable);
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/report")
    public ResponseEntity<ApiUtils.ApiResult<String>> report(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody @Valid ReportReplyReq request,
        Errors errors
    ) {
        User user = principalDetails.getUser();
        replyService.reportReply(request.toParamWith(user));
        return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.CREATED), HttpStatus.CREATED);
    }
}
