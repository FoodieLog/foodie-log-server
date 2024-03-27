package com.foodielog.application.user.controller.dto;

import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.type.Flag;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChangeNotificationParam {
    private User user;

    private Flag replyFlag;

    private Flag likeFlag;

    private Flag followFlag;
}
