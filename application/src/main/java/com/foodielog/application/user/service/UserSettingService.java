package com.foodielog.application.user.service;

import com.foodielog.application.user.dto.request.ChangeNotificationReq;
import com.foodielog.application.user.dto.request.ChangePasswordReq;
import com.foodielog.application.user.dto.request.ChangeProfileReq;
import com.foodielog.application.user.dto.request.WithdrawReq;
import com.foodielog.application.user.dto.response.*;
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
import com.foodielog.server.feed.type.ContentStatus;
import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.reply.repository.ReplyRepository;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;

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
    public ChangeNotificationResp changeNotification(User user, ChangeNotificationReq request) {
        user.changeNotificationFlag(request.getFlag());
        userRepository.save(user);

        return new ChangeNotificationResp(user, request.getFlag());
    }

    @Transactional(readOnly = true)
    public CheckBadgeApplyResp checkBadgeApply(User user) {
        Timestamp createdAt = badgeApplyRepository.findByUserId(user.getId())
                .map(BadgeApply::getCreatedAt)
                .orElseGet(() -> null);

        return new CheckBadgeApplyResp(user, createdAt);
    }

    @Transactional
    public CreateBadgeApplyResp creatBadgeApply(User user) {
        BadgeApply badgeApply = BadgeApply.createBadgeApply(user);
        badgeApplyRepository.save(badgeApply);

        return new CreateBadgeApplyResp(user, badgeApply);
    }

    @Transactional
    public ChangePasswordResp changePassword(User user, ChangePasswordReq request) {
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new Exception400("password", ErrorMessage.PASSWORD_NOT_MATCH);
        }

        user.resetPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        return new ChangePasswordResp(user.getEmail());
    }

    @Transactional
    public LogoutResp logout(String accessToken) {
        String email = invalidatedToken(accessToken);

        return new LogoutResp(email, Boolean.TRUE);
    }

    @Transactional
    public WithdrawResp withdraw(String accessToken, User user, WithdrawReq request) {
        // 탈퇴 유저 저장
        Long feedCount = feedRepository.countByUserAndStatus(user, ContentStatus.NORMAL);
        Long replyCount = replyRepository.countByUserAndStatus(user, ContentStatus.NORMAL);

        WithdrawUser withdrawUser = WithdrawUser.createWithdrawUser(user, feedCount, replyCount, request.getWithdrawReason());
        withdrawUserRepository.save(withdrawUser);

        // 유저, 피드, 댓글 상태 변경
        user.withdraw();
        userRepository.save(user);

        List<Feed> feedList = feedRepository.findByUserIdAndStatus(user.getId(), ContentStatus.NORMAL);
        feedList.forEach(Feed::deleteFeed);

        List<Reply> replyList = replyRepository.findByUserIdAndStatus(user.getId(), ContentStatus.NORMAL);
        replyList.forEach(Reply::deleteReply);

        // 토큰 무효화
        invalidatedToken(accessToken);

        return new WithdrawResp(user.getEmail(), Boolean.TRUE);
    }

    private String invalidatedToken(String accessToken) {
        Long expiration = jwtTokenProvider.getExpiration(accessToken);
        String email = jwtTokenProvider.getEmail(accessToken);

        redisService.addBlacklist(accessToken, email, expiration);
        return email;
    }

    @Transactional
    public ChangeProfileResp ChangeProfile(User user, ChangeProfileReq request, MultipartFile file) {
        // 기존 닉네임과 일치 하지 않은데 중복이라면 중복! (기존 닉네임과 동일 하다면 닉네임을 변경 하는게 아님)
        if (!user.getNickName().equals(request.getNickName())
                && userRepository.existsByNickName(request.getNickName())
        ) {
            throw new Exception400("nickName", "이미 사용 중인 닉네임 입니다");
        }

        String storedFileUrl = getStoredFileUrl(user.getProfileImageUrl(), file);
        user.changeProfile(request.getNickName(), storedFileUrl, request.getAboutMe());

        userRepository.save(user);

        return new ChangeProfileResp(user.getNickName(), user.getProfileImageUrl(), user.getAboutMe());
    }

    private String getStoredFileUrl(String userProfileImageUrl, MultipartFile file) {
        String storedFileUrl = userProfileImageUrl;

        if ((file != null)) {
            storedFileUrl = s3Uploader.saveFile(file);

            // 기존 이미지 삭제
            if (userProfileImageUrl != null) {
                s3Uploader.deleteFile(userProfileImageUrl);
            }
        }

        return storedFileUrl;
    }
}
