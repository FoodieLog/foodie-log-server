package com.foodielog.application.user.dto.request;

import com.foodielog.server._core.customValid.valid.ValidEnum;
import com.foodielog.server.admin.type.WithdrawReason;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WithdrawReq {
    @ValidEnum(enumClass = WithdrawReason.class)
    private WithdrawReason withdrawReason;
}