package com.foodielog.application.user.dto;

import lombok.Getter;


public class SendCodeDTO {
    @Getter
    public static class ForSignUpDTO {
        private final Response response;

        @Getter
        public static class Response {
            private final String email;

            public Response(String email) {
                this.email = email;
            }
        }

        public ForSignUpDTO(Response response) {
            this.response = response;
        }
    }

    @Getter
    public static class ForPassWordDTO {
        private final Response response;

        @Getter
        public static class Response {
            private final String email;

            public Response(String email) {
                this.email = email;
            }
        }

        public ForPassWordDTO(Response response) {
            this.response = response;
        }
    }
}
