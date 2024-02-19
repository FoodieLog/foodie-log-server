package com.foodielog.application.user.dto.request;

import com.foodielog.server._core.customValid.valid.ValidPassword;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChangePasswordReq {
    @ValidPassword
    private String oldPassword;

    @ValidPassword
    private String newPassword;
}

