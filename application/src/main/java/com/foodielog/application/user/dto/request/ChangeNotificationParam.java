package com.foodielog.application.user.dto.request;

import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.type.Flag;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChangeNotificationParam {
    private User user;

    private Flag flag;
}
