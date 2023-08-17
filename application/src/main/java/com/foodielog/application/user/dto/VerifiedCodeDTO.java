package com.foodielog.application.user.dto;

import lombok.Getter;


public class VerifiedCodeDTO {
    @Getter
    public static class Response {
        private final String email;
        private final String code;
        private final Boolean isVerified;

        public Response(String email, String code, Boolean isVerified) {
            this.email = email;
            this.code = code;
            this.isVerified = isVerified;
        }
    }
}
