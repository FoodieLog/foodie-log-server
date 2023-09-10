package com.foodielog.server.admin.entity;

import com.foodielog.server.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@DynamicInsert
@Table(name = "block_user_tb")
@Entity
public class BlockUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 100)
    private String reason;

    @Column(name = "feed_count")
    private Long feedCount;

    @Column(name = "reply_count")
    private Long replyCount;

    @CreationTimestamp
    private Timestamp createdAt;

    public static BlockUser createBlockByReport(User user, Long feedCount, Long replyCount) {
        BlockUser blockUser = new BlockUser();
        blockUser.user = user;
        blockUser.reason = "신고 승인 횟수 10번 초과";
        blockUser.feedCount = feedCount;
        blockUser.replyCount = replyCount;

        return blockUser;
    }

    public static BlockUser createBlock(User user, String reason, Long feedCount, Long replyCount) {
        BlockUser blockUser = new BlockUser();
        blockUser.user = user;
        blockUser.reason = reason;
        blockUser.feedCount = feedCount;
        blockUser.replyCount = replyCount;

        return blockUser;
    }
}
