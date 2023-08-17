package com.foodielog.application.user.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExistsEmailDTO {
    @Getter
    public static class Response {
        private final String email;
        private final Boolean isExists;

        public Response(String email, Boolean isExists) {
            this.email = email;
            this.isExists = isExists;
        }
    }
}
