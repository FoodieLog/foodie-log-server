package com.foodielog.application.user.dto;

import com.foodielog.server._core.customValid.valid.ValidPassWord;
import lombok.Getter;

import javax.validation.constraints.Email;

public class ResetPasswordDTO {
    @Getter
    public static class Request {
        @Email
        private String email;

        @ValidPassWord
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
