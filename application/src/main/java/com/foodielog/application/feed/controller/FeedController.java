package com.foodielog.application.feed.controller;

import com.foodielog.application.feed.dto.FeedRequest;
import com.foodielog.application.feed.service.FeedService;
import com.foodielog.server._core.kakaoApi.KakaoApiResponse;
import com.foodielog.server._core.security.auth.PrincipalDetails;
import com.foodielog.server._core.util.ApiUtils;
import com.foodielog.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class FeedController {

    private final FeedService feedService;
    
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String keyword) {
        KakaoApiResponse kakaoApiResponse = feedService.getSearch(keyword);
        return ResponseEntity.ok(ApiUtils.success(kakaoApiResponse));
    }

    @PostMapping("/save")
    public ResponseEntity<?> feedSave(
            @RequestBody @Valid FeedRequest.SaveDTO saveDTO,
            @RequestParam(value = "files", required = false) List<MultipartFile> files
            , @AuthenticationPrincipal PrincipalDetails userDetails) throws IOException {
        User user = userDetails.getUser();
        feedService.save(saveDTO, files, user);
        return ResponseEntity.ok(ApiUtils.success(null));
    }
}
