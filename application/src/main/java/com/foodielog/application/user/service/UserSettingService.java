package com.foodielog.application.user.service;

import com.foodielog.application.user.dto.ChangePasswordDTO;
import com.foodielog.server._core.error.ErrorMessage;
import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserSettingService {
    private final PasswordEncoder passwordEncoder;
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
}
