package com.foodielog.server.reply.entity;

import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.type.ContentStatus;
import com.foodielog.server.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "reply_tb")
@Entity
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @Column(length = 150, nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private ContentStatus status;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public static Reply createReply(User user, Feed feed, String content) {
        Reply reply = new Reply();
        reply.user = user;
        reply.feed = feed;
        reply.content = content;
        reply.status = ContentStatus.NORMAL;
        return reply;
    }

    public void deleteReply() {
        status = ContentStatus.DELETED;
    }

    public void deleteReplyByUser() {
        status = ContentStatus.HIDDEN;
    }

    public void restoreReply() {
        status = ContentStatus.NORMAL;
    }
}
