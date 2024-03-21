package com.foodielog.server.feed.entity;

import com.foodielog.server.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "feed_like_tb")
@Entity
public class FeedLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    private Timestamp createdAt;

    public static FeedLike createFeedLike(Feed feed, User user) {
        FeedLike feedLike = new FeedLike();
        feedLike.feed = feed;
        feedLike.user = user;
        return feedLike;
    }
}
