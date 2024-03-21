package com.foodielog.application.user.service.dto;

import com.foodielog.server.user.entity.User;
import lombok.Getter;

@Getter
public class ChangeProfileResp {
    private final String nickName;
    private final String profileImageUrl;
    private final String aboutMe;

    public ChangeProfileResp(User user) {
        this.nickName = user.getNickName();
        this.profileImageUrl = user.getProfileImageUrl();
        this.aboutMe = user.getAboutMe();
    }
}
