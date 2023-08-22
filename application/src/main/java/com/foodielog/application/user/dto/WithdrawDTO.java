package com.foodielog.application.user.dto;

import com.foodielog.server._core.customValid.valid.ValidEnum;
import com.foodielog.server.admin.type.WithdrawReason;
import lombok.Getter;

public class WithdrawDTO {
    @Getter
    public static class Request {
        @ValidEnum(enumClass = WithdrawReason.class)
        private WithdrawReason withdrawReason;
    }

    @Getter
    public static class Response {
        private final String email;
        private final Boolean success;

        public Response(String email, Boolean success) {
            this.email = email;
            this.success = success;
        }
    }
}
