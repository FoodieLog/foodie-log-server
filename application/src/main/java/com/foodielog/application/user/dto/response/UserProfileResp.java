package com.foodielog.application.user.dto.response;

import com.foodielog.server.user.entity.User;
import lombok.Getter;

@Getter
public class UserProfileResp {
    private final Long userId;
    private final String nickName;
    private final String profileImageUrl;
    private final String aboutMe;
    private final Long feedCount;
    private final Long follower;
    private final Long following;

    public UserProfileResp(User user, Long feedCount, Long follower, Long following) {
        this.userId = user.getId();
        this.nickName = user.getNickName();
        this.profileImageUrl = user.getProfileImageUrl();
        this.aboutMe = user.getAboutMe();
        this.feedCount = feedCount;
        this.follower = follower;
        this.following = following;
    }
}