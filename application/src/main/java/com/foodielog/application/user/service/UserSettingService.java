package com.foodielog.application.user.service;

import com.foodielog.application.user.dto.ChangeNotificationDTO;
import com.foodielog.application.user.dto.ChangePasswordDTO;
import com.foodielog.application.user.dto.ChangeProfileDTO;
import com.foodielog.application.user.dto.CheckBadgeApplyDTO;
import com.foodielog.server._core.error.ErrorMessage;
import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server._core.s3.S3Uploader;
import com.foodielog.server.admin.entity.BadgeApply;
import com.foodielog.server.admin.repository.BadgeApplyRepository;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserSettingService {
    private final PasswordEncoder passwordEncoder;
    private final S3Uploader s3Uploader;
    private final UserRepository userRepository;
    private final BadgeApplyRepository badgeApplyRepository;

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
    public ChangePasswordDTO.Response changePassword(User user, ChangePasswordDTO.Request request) {
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new Exception400("password", ErrorMessage.PASSWORD_NOT_MATCH);
        }

        user.resetPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        return new ChangePasswordDTO.Response(user.getEmail());
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
