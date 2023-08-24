package com.foodielog.application.feed.controller;

import com.foodielog.application.feed.dto.FeedSaveDTO;
import com.foodielog.application.feed.dto.LikeFeedDTO;
import com.foodielog.application.feed.dto.ReportFeedDTO;
import com.foodielog.application.feed.dto.UpdateFeedDTO;
import com.foodielog.application.feed.service.FeedService;
import com.foodielog.server._core.error.ErrorMessage;
import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server._core.kakaoApi.KakaoApiResponse;
import com.foodielog.server._core.kakaoApi.KakaoApiService;
import com.foodielog.server._core.security.auth.PrincipalDetails;
import com.foodielog.server._core.util.ApiUtils;
import com.foodielog.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/feed")
@RestController
public class FeedController {

    private final FeedService feedService;
    private final KakaoApiService kakaoApiService;

    @GetMapping("/search/restaurant")
    public ResponseEntity<ApiUtils.ApiResult<KakaoApiResponse>> useKakaoSearchApi(
            @RequestParam @NotBlank String keyword
    ) {
        log.info("kakao search keyword" + keyword);

        KakaoApiResponse kakaoApiResponse = kakaoApiService.getKakaoSearchApi(keyword);
        return new ResponseEntity<>(ApiUtils.success(kakaoApiResponse, HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<ApiUtils.ApiResult<String>> feedSave(
            @RequestPart(value = "content") @Valid FeedSaveDTO.Request request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal PrincipalDetails principalDetails, Errors errors
    ) {
        if (files == null || files.isEmpty()) {
            throw new Exception400("files", ErrorMessage.NO_SELECTED_IMAGE);
        }

        User user = principalDetails.getUser();
        feedService.save(request, files, user);

        return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/like")
    public ResponseEntity<ApiUtils.ApiResult<String>> like(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid LikeFeedDTO.Request request,
            Errors errors
    ) {
        feedService.likeFeed(principalDetails.getUser(), request.getFeedId());
        return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.OK), HttpStatus.OK);
    }

    @DeleteMapping("/unlike")
    public ResponseEntity<HttpStatus> unlike(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam @Positive Long feedId
    ) {
        feedService.unLikeFeed(principalDetails.getUser(), feedId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/delete")
    public ResponseEntity<ApiUtils.ApiResult<String>> delete(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam(name = "feed") @Positive Long feedId
    ) {
        User user = principalDetails.getUser();
        feedService.deleteFeed(user, feedId);
        return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.OK), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiUtils.ApiResult<String>> update(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid UpdateFeedDTO.Request request,
            Errors errors
    ) {
        User user = principalDetails.getUser();
        feedService.updateFeed(user, request);
        return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/report")
    public ResponseEntity<ApiUtils.ApiResult<String>> report(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid ReportFeedDTO.Request request,
            Errors errors
    ) {
        User user = principalDetails.getUser();
        feedService.reportFeed(user, request);
        return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.OK), HttpStatus.OK);
    }
}
