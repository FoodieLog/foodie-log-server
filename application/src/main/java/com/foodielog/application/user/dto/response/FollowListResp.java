package com.foodielog.application.user.dto.response;

import com.foodielog.server.user.entity.User;
import lombok.Getter;

import java.util.List;

@Getter
public class FollowListResp {
    private final List<FollowDTO> content;

    public FollowListResp(List<FollowDTO> content) {
        this.content = content;
    }

    @Getter
    public static class FollowDTO {
        private final Long userId;
        private final String nickName;
        private final String profileImageUrl;
        private final boolean isFollowed;

        public FollowDTO(User user, boolean isFollowed) {
            this.userId = user.getId();
            this.nickName = user.getNickName();
            this.profileImageUrl = user.getProfileImageUrl();
            this.isFollowed = isFollowed;
        }
    }
}
