package com.foodielog.application.user.dto.request;

import com.foodielog.server._core.customValid.valid.ValidPassword;
import lombok.Getter;

import javax.validation.constraints.Email;

@Getter
public class ResetPasswordReq {
    @Email
    private String email;

    @ValidPassword
    private String password;

    public ResetPasswordParam toParam(){
        return ResetPasswordParam.builder()
            .email(email)
            .password(password)
            .build();
    }
}