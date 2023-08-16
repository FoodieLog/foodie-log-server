package com.foodielog.application.user.dto.response;

import com.foodielog.server.feed.entity.Feed;
import lombok.Getter;

import java.util.List;

public class UserThumbnailDTO {
    @Getter
    public static class Response {
        private final List<ThumbnailDTO> content;

        public Response(List<ThumbnailDTO> content) {
            this.content = content;
        }
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
