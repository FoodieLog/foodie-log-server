package com.foodielog.application.user.controller.dto;

import com.foodielog.server._core.customValid.valid.ValidEnum;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.type.Flag;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChangeNotificationReq {
    @ValidEnum(enumClass = Flag.class)
    private Flag replyFlag;

    @ValidEnum(enumClass = Flag.class)
    private Flag likeFlag;

    @ValidEnum(enumClass = Flag.class)
    private Flag followFlag;

    public ChangeNotificationParam toParamWith(User user) {
        return ChangeNotificationParam.builder()
                .user(user)
                .replyFlag(replyFlag)
                .likeFlag(likeFlag)
                .followFlag(followFlag)
                .build();
    }
}
