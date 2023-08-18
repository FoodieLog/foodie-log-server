package com.foodielog.application.user.dto;

import com.foodielog.server._core.customValid.valid.ValidPassword;
import lombok.Getter;

public class ChangePasswordDTO {
    @Getter
    public static class Request {
        @ValidPassword
        private String oldPassword;

        @ValidPassword
        private String newPassword;
    }

    @Getter
    public static class Response {
        private final String password;

        public Response(String password) {
            this.password = password;
        }
    }
}
