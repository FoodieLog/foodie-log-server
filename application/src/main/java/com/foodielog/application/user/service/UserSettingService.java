package com.foodielog.application.user.service;

import com.foodielog.application.user.dto.ChangePasswordDTO;
import com.foodielog.application.user.dto.ChangeProfileDTO;
import com.foodielog.server._core.error.ErrorMessage;
import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server._core.s3.S3Uploader;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserSettingService {
    private final PasswordEncoder passwordEncoder;
    private final S3Uploader s3Uploader;
    private final UserRepository userRepository;

    @Transactional
    public ChangePasswordDTO.Response changePassword(User user, ChangePasswordDTO.Request request) {
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new Exception400("password", ErrorMessage.PASSWORD_NOT_MATCH);
        }

        user.resetPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        return new ChangePasswordDTO.Response(user.getEmail());
    }

    public ChangeProfileDTO.Response ChangeProfile(User user, ChangeProfileDTO.Request request, MultipartFile file) {
        if (userRepository.existsByNickName(request.getNickName())) {
            throw new Exception400("nickName", "이미 사용 중인 닉네임 입니다");
        }

        String storedFileUrl = "";
        if (!file.isEmpty()) {
            storedFileUrl = s3Uploader.saveFile(file);
        }

        user.changeProfile(request.getNickName(), storedFileUrl, request.getAboutMe());

        userRepository.save(user);

        return new ChangeProfileDTO.Response(user.getNickName(), user.getProfileImageUrl(), user.getAboutMe());
    }
}
