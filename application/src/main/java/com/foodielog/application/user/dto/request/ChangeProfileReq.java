package com.foodielog.application.user.dto.request;

import com.foodielog.server._core.customValid.valid.ValidNickName;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChangeProfileReq {
    @ValidNickName
    private String nickName;

    private String aboutMe;
}
