package com.foodielog.application.user.dto.response;

import com.foodielog.server.admin.entity.BadgeApply;
import com.foodielog.server.user.entity.User;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class CreateBadgeApplyResp {
    private final String nickName;
    private final Timestamp createdAt;

    public CreateBadgeApplyResp(User user, BadgeApply badgeApply) {
        this.nickName = user.getNickName();
        this.createdAt = badgeApply.getCreatedAt();
    }
}