package com.foodielog.application.user.dto.request;

import com.foodielog.server._core.customValid.valid.ValidNickName;
import lombok.Getter;

@Getter
public class ChangeProfileReq {
    @ValidNickName
    private String nickName;

    private String aboutMe;
}
