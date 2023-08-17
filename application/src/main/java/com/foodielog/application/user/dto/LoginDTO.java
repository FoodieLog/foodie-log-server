package com.foodielog.application.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foodielog.server._core.customValid.valid.ValidPassWord;
import com.foodielog.server.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginDTO {
    @Getter
    public static class Request {
        @Email
        private String email;

        @ValidPassWord
        private String password;
    }

    @Getter
    public static class Response {
        private final String nickName;
        private final String profileImageUrl;
        private final String accessToken;

        @JsonIgnore
        private final String refreshToken;

        public Response(User user, String accessToken, String refreshToken) {
            this.nickName = user.getNickName();
            this.profileImageUrl = user.getProfileImageUrl();
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }
}