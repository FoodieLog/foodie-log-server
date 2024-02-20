package com.foodielog.application.feed.service.dto;

import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.entity.Media;
import com.foodielog.server.restaurant.entity.Restaurant;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;

@Getter
public class MainFeedListResp {
    private final List<MainFeedsDTO> content;

    public MainFeedListResp(List<MainFeedsDTO> content) {
        this.content = content;
    }

    @Getter
    public static class MainFeedsDTO {
        private final FeedDTO feed;
        private final MainFeedRestaurantDTO restaurant;
        private final boolean isFollowed;
        private final boolean isLiked;

        public MainFeedsDTO(FeedDTO feed, MainFeedRestaurantDTO restaurant, boolean isFollowed, boolean isLiked) {
            this.feed = feed;
            this.restaurant = restaurant;
            this.isFollowed = isFollowed;
            this.isLiked = isLiked;
        }
    }

    @Getter
    public static class FeedDTO {
        private final Long userId;
        private final String nickName;
        private final String profileImageUrl;
        private final Long feedId;
        private final Timestamp createdAt;
        private final Timestamp updatedAt;
        private final List<FeedImageDTO> feedImages;
        private final String content;
        private final Long likeCount;
        private final Long replyCount;

        public FeedDTO(Feed feed, List<FeedImageDTO> feedImages, Long likeCount, Long replyCount) {
            this.userId = feed.getUser().getId();
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
    }

    @Getter
    public static class FeedImageDTO {
        private final String imageUrl;

        public FeedImageDTO(Media media) {
            this.imageUrl = media.getImageUrl();
        }
    }

    @Getter
    public static class MainFeedRestaurantDTO {
        private final Long id;
        private final String name;
        private final String category;
        private final String link;
        private final String roadAddress;

        public MainFeedRestaurantDTO(Restaurant restaurant) {
            this.id = restaurant.getId();
            this.name = restaurant.getName();
            this.category = restaurant.getCategory();
            this.link = restaurant.getLink();
            this.roadAddress = restaurant.getRoadAddress();
        }
    }
}