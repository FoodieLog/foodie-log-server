package com.foodielog.application.feed.controller;

import com.foodielog.application.feed.dto.request.FeedSaveReq;
import com.foodielog.application.feed.dto.request.LikeFeedReq;
import com.foodielog.application.feed.dto.request.ReportFeedReq;
import com.foodielog.application.feed.dto.request.UpdateFeedReq;
import com.foodielog.application.feed.dto.response.FeedDetailResp;
import com.foodielog.application.feed.dto.response.MainFeedListResp;
import com.foodielog.application.feed.service.FeedService;
import com.foodielog.server._core.kakaoApi.KakaoApiResponse;
import com.foodielog.server._core.kakaoApi.KakaoApiService;
import com.foodielog.server._core.security.auth.PrincipalDetails;
import com.foodielog.server._core.util.ApiUtils;
import com.foodielog.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import javax.validation.constraints.PositiveOrZero;
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
            @RequestPart(value = "content") @Valid FeedSaveReq request,
            @RequestPart(value = "files") List<MultipartFile> files,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            Errors errors
    ) {
        User user = principalDetails.getUser();
        feedService.save(request, files, user);

        return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.CREATED), HttpStatus.CREATED);
    }

    @PostMapping("/like")
    public ResponseEntity<ApiUtils.ApiResult<String>> like(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid LikeFeedReq request,
            Errors errors
    ) {
        feedService.likeFeed(principalDetails.getUser(), request.getFeedId());
        return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.CREATED), HttpStatus.CREATED);
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
            @RequestParam @Positive Long feedId
    ) {
        User user = principalDetails.getUser();
        feedService.deleteFeed(user, feedId);
        return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.OK), HttpStatus.OK);
    }

    @PatchMapping("/update")
    public ResponseEntity<ApiUtils.ApiResult<String>> update(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid UpdateFeedReq request,
            Errors errors
    ) {
        User user = principalDetails.getUser();
        feedService.updateFeed(user, request);
        return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/report")
    public ResponseEntity<ApiUtils.ApiResult<String>> report(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid ReportFeedReq request,
            Errors errors
    ) {
        User user = principalDetails.getUser();
        feedService.reportFeed(user, request);
        return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.CREATED), HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<ApiUtils.ApiResult<MainFeedListResp>> list(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam @PositiveOrZero Long feedId,
            @PageableDefault(size = 15) Pageable pageable
    ) {
        MainFeedListResp response = feedService.getMainFeed(principalDetails.getUser(), feedId, pageable);
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/detail")
    public ResponseEntity<ApiUtils.ApiResult<FeedDetailResp>> detail(
            @RequestParam @Positive Long feedId
    ) {
        FeedDetailResp response = feedService.getFeedDetail(feedId);
        return new ResponseEntity<>(ApiUtils.success(response, HttpStatus.OK), HttpStatus.OK);
    }
}
