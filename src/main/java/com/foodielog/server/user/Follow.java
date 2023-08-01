package com.foodielog.server.user;

import com.foodielog.server.notification.Notification;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;

public class Follow {
    @Id
    @Column(name = "following_id")
    private User followingId; // 나

    @Id
    @Column(name = "followed_id")
    private User followedId; // 너

    @ManyToOne
    @JoinColumn(name = "notification")
    private Notification notification;

    @CreationTimestamp
    private Timestamp createdAt; // 나 -> 너
}
