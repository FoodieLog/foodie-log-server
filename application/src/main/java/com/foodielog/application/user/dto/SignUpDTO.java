package com.foodielog.application.user.dto;

import com.foodielog.server._core.customValid.valid.ValidNickName;
import com.foodielog.server._core.customValid.valid.ValidPassword;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SignUpDTO {
    @Getter
    public static class Request {
        @Email
        private String email;

        @ValidPassword
        private String password;

        @ValidNickName
        private String nickName;

        private String aboutMe;
    }

    @Getter
    public static class Response {
        private final String email;
        private final String nickName;
        private final String profileImageUrl;

        public Response(String email, String nickName, String profileImageUrl) {
            this.email = email;
            this.nickName = nickName;
            this.profileImageUrl = profileImageUrl;
        }
    }
}
