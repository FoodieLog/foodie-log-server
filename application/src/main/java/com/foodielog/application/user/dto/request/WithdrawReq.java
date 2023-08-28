package com.foodielog.application.user.dto.request;

import com.foodielog.server._core.customValid.valid.ValidEnum;
import com.foodielog.server.admin.type.WithdrawReason;
import lombok.Getter;

@Getter
public class WithdrawReq {
    @ValidEnum(enumClass = WithdrawReason.class)
    private WithdrawReason withdrawReason;
}