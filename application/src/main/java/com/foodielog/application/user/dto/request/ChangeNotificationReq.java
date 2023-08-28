package com.foodielog.application.user.dto.request;

import com.foodielog.server._core.customValid.valid.ValidEnum;
import com.foodielog.server.user.type.Flag;
import lombok.Getter;

@Getter
public class ChangeNotificationReq {
    @ValidEnum(enumClass = Flag.class)
    private Flag flag;
}
