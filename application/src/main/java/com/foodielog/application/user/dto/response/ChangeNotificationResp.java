package com.foodielog.application.user.dto.response;

import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.type.Flag;
import lombok.Getter;

@Getter
public class ChangeNotificationResp {
    private final String nickName;
    private final Flag flag;

    public ChangeNotificationResp(User user, Flag flag) {
        this.nickName = user.getNickName();
        this.flag = flag;
    }
}
