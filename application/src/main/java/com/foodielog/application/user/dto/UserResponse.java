package com.foodielog.application.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foodielog.server.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {
    @Getter
    public static class ExistsEmailDTO {
        private final String email;

        public ExistsEmailDTO(String email) {
            this.email = email;
        }
    }

    @Getter
    public static class ExistsNickNameDTO {
        private final String nickName;

        public ExistsNickNameDTO(String nickName) {
            this.nickName = nickName;
        }
    }

    @Getter
    public static class LoginDTO {
        private final String nickName;
        private final String profileImageUrl;
        private final String accessToken;

        @JsonIgnore
        private final String refreshToken;

        public LoginDTO(User user, String accessToken, String refreshToken) {
            this.nickName = user.getNickName();
            this.profileImageUrl = user.getProfileImageUrl();
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }
}
