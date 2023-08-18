package com.foodielog.application.user.dto;

import com.foodielog.server._core.customValid.valid.ValidPassword;
import lombok.Getter;

import javax.validation.constraints.Email;

public class ResetPasswordDTO {
    @Getter
    public static class Request {
        @Email
        private String email;

        @ValidPassword
        private String password;
    }

    @Getter
    public static class Response {
        private final String email;

        public Response(String email) {
            this.email = email;
        }
    }
}
