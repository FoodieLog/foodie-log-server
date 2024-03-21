package com.foodielog.application.user.service;

import com.foodielog.application.badgeApply.service.BadgeApplyModuleService;
import com.foodielog.application.feed.service.FeedModuleService;
import com.foodielog.application.reply.service.ReplyModuleService;
import com.foodielog.application.user.controller.dto.ChangeNotificationParam;
import com.foodielog.application.user.controller.dto.ChangePasswordParam;
import com.foodielog.application.user.controller.dto.ChangeProfileParam;
import com.foodielog.application.user.controller.dto.WithdrawParam;
import com.foodielog.application.user.service.dto.*;
import com.foodielog.application.withdrawUser.service.WithdrawUserModuleService;
import com.foodielog.server._core.error.ErrorMessage;
import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server._core.s3.S3Uploader;
import com.foodielog.server._core.security.jwt.JwtTokenProvider;
import com.foodielog.server.admin.entity.BadgeApply;
import com.foodielog.server.admin.entity.WithdrawUser;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.user.entity.User;
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
    private final S3Uploader s3Uploader;

    private final UserModuleService userModuleService;
    private final BadgeApplyModuleService badgeApplyModuleService;
    private final FeedModuleService feedModuleService;
    private final ReplyModuleService replyModuleService;
    private final WithdrawUserModuleService withdrawUserModuleService;

    @Transactional
    public ChangeNotificationResp changeNotification(ChangeNotificationParam parameter) {
        User user = parameter.getUser();
        user.changeNotificationFlag(parameter.getFlag());
        user = userModuleService.save(user);

        return new ChangeNotificationResp(user);
    }

    @Transactional(readOnly = true)
    public CheckBadgeApplyResp checkBadgeApply(User user) {
        Timestamp createdAt = badgeApplyModuleService.getCreatedAt(user.getId());
        return new CheckBadgeApplyResp(user, createdAt);
    }

    @Transactional
    public CreateBadgeApplyResp creatBadgeApply(User user) {
        BadgeApply badgeApply = BadgeApply.createBadgeApply(user);
        badgeApplyModuleService.save(badgeApply);

        return new CreateBadgeApplyResp(user, badgeApply);
    }

    @Transactional
    public ChangePasswordResp changePassword(ChangePasswordParam parameter) {
        User user = parameter.getUser();

        if (!passwordEncoder.matches(parameter.getOldPassword(), user.getPassword())) {
            throw new Exception400("password", ErrorMessage.PASSWORD_NOT_MATCH);
        }

        user.resetPassword(passwordEncoder.encode(parameter.getNewPassword()));
        user = userModuleService.save(user);

        return new ChangePasswordResp(user);
    }

    @Transactional
    public LogoutResp logout(String accessToken) {
        String email = jwtTokenProvider.invalidateToken(accessToken);

        return new LogoutResp(email, Boolean.TRUE);
    }

    @Transactional
    public WithdrawResp withdraw(WithdrawParam parameter) {
        User user = parameter.getUser();

        // 탈퇴 유저 저장
        Long feedCount = feedModuleService.getUserCount(user);
        Long replyCount = replyModuleService.getUserCount(user);

        WithdrawUser withdrawUser = WithdrawUser.createWithdrawUser(
                user, feedCount, replyCount, parameter.getWithdrawReason()
        );
        withdrawUserModuleService.save(withdrawUser);

        // 유저, 피드, 댓글 상태 변경
        user.withdraw();
        user = userModuleService.save(user);

        List<Feed> feedList = feedModuleService.getUserFeeds(user);
        feedList.forEach(Feed::deleteFeed);

        List<Reply> replyList = replyModuleService.getUserReplys(user);
        replyList.forEach(Reply::deleteReply);

        // 토큰 무효화
        jwtTokenProvider.invalidateToken(parameter.getAccessToken());

        return new WithdrawResp(user, Boolean.TRUE);
    }

    @Transactional
    public ChangeProfileResp ChangeProfile(ChangeProfileParam parameter) {
        User user = parameter.getUser();

        // 닉네임 변경하는 경우 중복체크
        if (!user.getNickName().equals(parameter.getNickName())) {
            userModuleService.checkNewNickName(parameter.getNickName());
        }

        String storedFileUrl = getStoredFileUrl(user.getProfileImageUrl(), parameter.getFile());
        user.changeProfile(parameter.getNickName(), storedFileUrl, parameter.getAboutMe());
        user = userModuleService.save(user);

        return new ChangeProfileResp(user);
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
