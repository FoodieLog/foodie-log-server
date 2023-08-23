package com.foodielog.application.reply.controller;

import com.foodielog.application.reply.dto.ReplyCreatDTO;
import com.foodielog.application.reply.service.ReplyService;
import com.foodielog.server._core.security.auth.PrincipalDetails;
import com.foodielog.server._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/api/reply")
@RestController
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/{feedId}")
    public ResponseEntity<?> saveReply(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                       @PathVariable Long feedId,
                                       @Valid @RequestBody ReplyCreatDTO.Request createDTO,
                                       Errors errors) {
        ReplyCreatDTO.Response response = replyService.createReply(principalDetails.getUser(), feedId, createDTO);
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.CREATED), HttpStatus.CREATED);
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<?> deleteReply(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                         @PathVariable Long replyId) {
        replyService.deleteReply(principalDetails.getUser(), replyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{feedId}")
    public ResponseEntity<?> getReplyList(@PathVariable Long feedId,
                                          @RequestParam Long replyId,
                                          @PageableDefault Pageable pageable) {
        ReplyCreatDTO.ListDTO response = replyService.getListReply(feedId, replyId, pageable);
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }
}
