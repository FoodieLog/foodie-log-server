package com.foodielog.application.user.dto;

import com.foodielog.server._core.customValid.valid.ValidEnum;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.type.Flag;
import lombok.Getter;

public class ChangeNotificationDTO {
    @Getter
    public static class Request {
        @ValidEnum(enumClass = Flag.class)
        private Flag flag;
    }

    @Getter
    public static class Response {
        private final String nickName;
        private final Flag flag;

        public Response(User user, Flag flag) {
            this.nickName = user.getNickName();
            this.flag = flag;
        }
    }
}
