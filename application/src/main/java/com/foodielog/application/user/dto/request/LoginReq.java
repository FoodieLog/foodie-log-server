package com.foodielog.application.user.dto.request;

import com.foodielog.server._core.customValid.valid.ValidPassword;
import lombok.Getter;

import javax.validation.constraints.Email;

@Getter
public class LoginReq {
    @Email
    private String email;

    @ValidPassword
    private String password;

    public LoginParam toParam(){
        return LoginParam.builder()
            .email(email)
            .password(password)
            .build();
    }
}
