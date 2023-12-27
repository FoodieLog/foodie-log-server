package com.foodielog.server.user.entity;

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
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id")
    private User followingId; // 나

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id")
    private User followedId; // 너

    @CreationTimestamp
    private Timestamp createdAt; // 나 -> 너

    public static Follow createFollow(User following, User followed) {
        Follow follow = new Follow();
        follow.followingId = following;
        follow.followedId = followed;
        return follow;
    }
}
