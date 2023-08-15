package com.foodielog.application.feed.controller;

import com.foodielog.application.feed.dto.FeedRequest;
import com.foodielog.application.feed.service.FeedService;
import com.foodielog.server._core.error.ErrorMessage;
import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server._core.error.exception.Exception500;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class FeedController {

    private final FeedService feedService;
    private final KakaoApiService kakaoApiService;

    @GetMapping("/search")
    public ResponseEntity<?> useKakaoSearchApi(@RequestParam String keyword) {
        log.info("kakao search keyword" + keyword);

        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiUtils.error("검색어를 입력해주세요.", HttpStatus.BAD_REQUEST));
        }

        KakaoApiResponse kakaoApiResponse = kakaoApiService.getKakaoSearchApi(keyword);
        return new ResponseEntity<>(ApiUtils.success(kakaoApiResponse, HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/feed")
    public ResponseEntity<?> feedSave(@RequestPart(value = "content") @Valid FeedRequest.SaveDTO saveDTO,
                                      @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                      @AuthenticationPrincipal PrincipalDetails principalDetails, Errors errors) {
        if (files == null || files.isEmpty()) {
            throw new Exception400("files", ErrorMessage.NO_SELECTED_IMAGE);
        }
        // TODO : 이미지 url이 80 이상인 경우 s3에는 업로드 되고, db에는 url 길이 제한으로 저장에 실패하는 경우 고려.
        try {
            User user = principalDetails.getUser();
            feedService.save(saveDTO, files, user);

            return new ResponseEntity<>(ApiUtils.success(null, HttpStatus.OK), HttpStatus.OK);
        } catch (IOException e) {
            throw new Exception500(ErrorMessage.FAIL_UPLOAD);
        }
    }
}
