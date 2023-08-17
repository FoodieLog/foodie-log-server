package com.foodielog.application.user.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExistsNickNameDTO {
    @Getter
    public static class Response {
        private final String nickName;
        private final Boolean isExists;

        public Response(String nickName, Boolean isExists) {
            this.nickName = nickName;
            this.isExists = isExists;
        }
    }
}
