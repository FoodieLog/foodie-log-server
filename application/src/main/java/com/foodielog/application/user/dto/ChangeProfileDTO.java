package com.foodielog.application.user.dto;

import com.foodielog.server._core.customValid.valid.ValidNickName;
import lombok.Getter;

public class ChangeProfileDTO {
    @Getter
    public static class Request {
        @ValidNickName
        private String nickName;

        private String aboutMe;
    }

    @Getter
    public static class Response {
        private final String nickName;
        private final String profileImageUrl;
        private final String aboutMe;

        public Response(String nickName, String profileImageUrl, String aboutMe) {
            this.nickName = nickName;
            this.profileImageUrl = profileImageUrl;
            this.aboutMe = aboutMe;
        }
    }
}
