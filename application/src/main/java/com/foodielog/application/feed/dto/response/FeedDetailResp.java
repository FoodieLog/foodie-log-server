package com.foodielog.application.feed.dto.response;

import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.entity.Media;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;

@Getter
public class FeedDetailResp {
    private final String nickName;
    private final String profileImageUrl;
    private final Long feedId;
    private final Timestamp createdAt;
    private final Timestamp updatedAt;
    private final List<FeedImageDTO> feedImages;
    private final String content;
    private final Long likeCount;
    private final Long replyCount;

    public FeedDetailResp(Feed feed, List<FeedImageDTO> feedImages, Long likeCount, Long replyCount) {
        this.nickName = feed.getUser().getNickName();
        this.profileImageUrl = feed.getUser().getProfileImageUrl();
        this.feedId = feed.getId();
        this.createdAt = feed.getCreatedAt();
        this.updatedAt = feed.getUpdatedAt();
        this.feedImages = feedImages;
        this.content = feed.getContent();
        this.likeCount = likeCount;
        this.replyCount = replyCount;
    }

    @Getter
    public static class FeedImageDTO {
        private final String imageUrl;

        public FeedImageDTO(Media media) {
            this.imageUrl = media.getImageUrl();
        }
    }
}