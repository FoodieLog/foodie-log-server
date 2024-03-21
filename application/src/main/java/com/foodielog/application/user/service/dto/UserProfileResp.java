package com.foodielog.application.user.service.dto;

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
    private final boolean isFollowed;

    public UserProfileResp(User user, Long feedCount, Long follower, Long following, boolean isFollowed) {
        this.userId = user.getId();
        this.nickName = user.getNickName();
        this.profileImageUrl = user.getProfileImageUrl();
        this.aboutMe = user.getAboutMe();
        this.feedCount = feedCount;
        this.follower = follower;
        this.following = following;
        this.isFollowed = isFollowed;
    }
}