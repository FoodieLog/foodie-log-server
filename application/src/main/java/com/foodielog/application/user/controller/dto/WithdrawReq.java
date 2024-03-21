package com.foodielog.application.user.controller.dto;

import com.foodielog.server._core.customValid.valid.ValidEnum;
import com.foodielog.server.admin.type.WithdrawReason;
import com.foodielog.server.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WithdrawReq {
    @ValidEnum(enumClass = WithdrawReason.class)
    private WithdrawReason withdrawReason;

    public WithdrawParam toParamWith(String accessToken, User user) {
        return WithdrawParam.builder()
                .accessToken(accessToken)
                .user(user)
                .withdrawReason(withdrawReason)
                .build();
    }
}