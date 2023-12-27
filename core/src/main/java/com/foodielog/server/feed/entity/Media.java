package com.foodielog.server.feed.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "media_tb")
@Entity
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @CreationTimestamp
    private Timestamp createdAt;

    public static Media createMedia(Feed feed, String imageUrl) {
        Media media = new Media();
        media.feed = feed;
        media.imageUrl = imageUrl;
        return media;
    }
}
