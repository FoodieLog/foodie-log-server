package com.foodielog.application.user.service;

import com.foodielog.application.user.dto.*;
import com.foodielog.server._core.error.ErrorMessage;
import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server._core.redis.RedisService;
import com.foodielog.server._core.s3.S3Uploader;
import com.foodielog.server._core.security.jwt.JwtTokenProvider;
import com.foodielog.server.admin.entity.BadgeApply;
import com.foodielog.server.admin.entity.WithdrawUser;
import com.foodielog.server.admin.repository.BadgeApplyRepository;
import com.foodielog.server.admin.repository.WithdrawUserRepository;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.repository.FeedRepository;
import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.reply.repository.ReplyRepository;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserSettingService {
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final S3Uploader s3Uploader;

    private final UserRepository userRepository;
    private final FeedRepository feedRepository;
    private final ReplyRepository replyRepository;
    private final BadgeApplyRepository badgeApplyRepository;
    private final WithdrawUserRepository withdrawUserRepository;

    @Transactional
    public ChangeNotificationDTO.Response changeNotification(User user, ChangeNotificationDTO.Request request) {
        user.changeNotificationFlag(request.getFlag());
        userRepository.save(user);

        return new ChangeNotificationDTO.Response(user, request.getFlag());
    }

    @Transactional(readOnly = true)
    public CheckBadgeApplyDTO.Response checkBadgeApply(User user) {
        Timestamp createdAt = badgeApplyRepository.findByUserId(user.getId())
                .map(BadgeApply::getCreatedAt)
                .orElseGet(() -> null);

        return new CheckBadgeApplyDTO.Response(user, createdAt);
    }

    @Transactional
    public CreateBadgeApplyDTO.Response creatBadgeApply(User user) {
        BadgeApply badgeApply = BadgeApply.createBadgeApply(user);
        badgeApplyRepository.save(badgeApply);

        return new CreateBadgeApplyDTO.Response(user, badgeApply);
    }

    @Transactional
    public ChangePasswordDTO.Response changePassword(User user, ChangePasswordDTO.Request request) {
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new Exception400("password", ErrorMessage.PASSWORD_NOT_MATCH);
        }

        user.resetPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        return new ChangePasswordDTO.Response(user.getEmail());
    }

    @Transactional
    public LogoutDTO.Response logout(String accessToken) {
        String email = invalidatedToken(accessToken);

        return new LogoutDTO.Response(email, Boolean.TRUE);
    }

    @Transactional
    public WithdrawDTO.Response withdraw(String accessToken, User user, WithdrawDTO.Request request) {
        // 탈퇴 유저 저장
        Long feedCount = feedRepository.countByUser(user);
        Long replyCount = replyRepository.countByUser(user);

        WithdrawUser withdrawUser = WithdrawUser.createWithdrawUser(user, feedCount, replyCount, request.getWithdrawReason());
        withdrawUserRepository.save(withdrawUser);

        // 유저, 피드, 댓글 상태 변경
        user.withdraw();
        userRepository.save(user);

        List<Feed> feedList = feedRepository.findByUserId(user.getId());
        feedList.forEach(Feed::deleteFeed);

        List<Reply> replyList = replyRepository.findByUserId(user.getId());
        replyList.forEach(Reply::deleteReply);

        // 토큰 무효화
        invalidatedToken(accessToken);

        return new WithdrawDTO.Response(user.getEmail(), Boolean.TRUE);
    }

    private String invalidatedToken(String accessToken) {
        Long expiration = jwtTokenProvider.getExpiration(accessToken);
        String email = jwtTokenProvider.getEmail(accessToken);

        // 리프레시 토큰 삭제
        redisService.deleteByKey(RedisService.REFRESH_TOKEN_PREFIX + email);
        // 엑세스 토큰은 만료 시점까지 블랙리스트 등록
        redisService.setObjectByKey(accessToken, RedisService.LOGOUT_VALUE_PREFIX, expiration, TimeUnit.MILLISECONDS);
        return email;
    }

    @Transactional
    public ChangeProfileDTO.Response ChangeProfile(User user, ChangeProfileDTO.Request request, MultipartFile file) {
        // 기존 닉네임과 일치 하지 않은데 중복이라면 중복! (기존 닉네임과 동일 하다면 닉네임을 변경 하는게 아님)
        if (!user.getNickName().equals(request.getNickName())
                && userRepository.existsByNickName(request.getNickName())
        ) {
            throw new Exception400("nickName", "이미 사용 중인 닉네임 입니다");
        }

        String storedFileUrl = null;
        if (!file.isEmpty()) {
            storedFileUrl = s3Uploader.saveFile(file);

            if (user.getProfileImageUrl() != null) {
                s3Uploader.deleteFile(user.getProfileImageUrl());
            }
        }

        user.changeProfile(request.getNickName(), storedFileUrl, request.getAboutMe());

        userRepository.save(user);

        return new ChangeProfileDTO.Response(user.getNickName(), user.getProfileImageUrl(), user.getAboutMe());
    }
}
