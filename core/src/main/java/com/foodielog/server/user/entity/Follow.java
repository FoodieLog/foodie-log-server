package com.foodielog.server.user.entity;

import com.foodielog.server.notification.entity.Notification;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "follow_tb")
@Entity
@IdClass(FollowPK.class)
public class Follow {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id")
    private User followingId; // 나

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id")
    private User followedId; // 너

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification")
    private Notification notification;

    @CreationTimestamp
    private Timestamp createdAt; // 나 -> 너
}
