package com.foodielog.application.user.dto.response;

import lombok.Getter;


public class SendCodeDTO {
    @Getter
    public static class Response {
        private final String email;

        public Response(String email) {
            this.email = email;
        }
    }
}
