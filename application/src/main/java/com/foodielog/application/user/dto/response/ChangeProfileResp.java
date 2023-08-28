package com.foodielog.application.user.dto.response;

import lombok.Getter;

@Getter
public class ChangeProfileResp {
    private final String nickName;
    private final String profileImageUrl;
    private final String aboutMe;

    public ChangeProfileResp(String nickName, String profileImageUrl, String aboutMe) {
        this.nickName = nickName;
        this.profileImageUrl = profileImageUrl;
        this.aboutMe = aboutMe;
    }
}
