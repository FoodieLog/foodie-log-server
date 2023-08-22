package com.foodielog.application.user.dto;

import lombok.Getter;

public class LogoutDTO {
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
