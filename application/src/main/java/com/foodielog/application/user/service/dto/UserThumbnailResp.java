package com.foodielog.application.user.service.dto;

import com.foodielog.server.feed.entity.Feed;
import lombok.Getter;

import java.util.List;

@Getter
public class UserThumbnailResp {
    private final List<ThumbnailDTO> content;

    public UserThumbnailResp(List<ThumbnailDTO> content) {
        this.content = content;
    }

    @Getter
    public static class ThumbnailDTO {
        private final Long id;
        private final String thumbnailUrl;

        public ThumbnailDTO(Feed feed) {
            this.id = feed.getId();
            this.thumbnailUrl = feed.getThumbnailUrl();
        }
    }
}

