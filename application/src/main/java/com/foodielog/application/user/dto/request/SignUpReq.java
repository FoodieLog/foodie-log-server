package com.foodielog.application.user.dto.request;

import com.foodielog.server._core.customValid.valid.ValidNickName;
import com.foodielog.server._core.customValid.valid.ValidPassword;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;

@Builder
@Getter
public class SignUpReq {
    @Email
    private String email;

    @ValidPassword
    private String password;

    @ValidNickName
    private String nickName;

    private String aboutMe;
}