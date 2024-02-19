package com.foodielog.application.user.dto.request;

import com.foodielog.server.admin.type.WithdrawReason;
import com.foodielog.server.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WithdrawParam {
    private String accessToken;
    private User user;
    private WithdrawReason withdrawReason;
}