package com.foodielog.application.user.service.dto;

import com.foodielog.server.user.entity.User;
import lombok.Getter;

import java.util.List;

@Getter
public class FollowerListResp {
    private final List<FollowerDTO> content;

    public FollowerListResp(List<FollowerDTO> content) {
        this.content = content;
    }

    @Getter
    public static class FollowerDTO {
        private final Long userId;
        private final String nickName;
        private final String profileImageUrl;
        private final boolean isFollowed;

        public FollowerDTO(User user, boolean isFollowed) {
            this.userId = user.getId();
            this.nickName = user.getNickName();
            this.profileImageUrl = user.getProfileImageUrl();
            this.isFollowed = isFollowed;
        }
    }
}
