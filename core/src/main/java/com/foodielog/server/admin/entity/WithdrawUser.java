package com.foodielog.server.admin.entity;

import com.foodielog.server.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "withdraw_user_tb")
@Entity
public class WithdrawUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "feed_count")
    private Long feedCount;

    @Column(name = "reply_count")
    private Long replyCount;

    @CreationTimestamp
    private Timestamp createdAt;

    public static WithdrawUser createWithdrawUser(User user, Long feedCount, Long replyCount) {
        WithdrawUser withdrawUser = new WithdrawUser();
        withdrawUser.user = user;
        withdrawUser.feedCount = feedCount;
        withdrawUser.replyCount = replyCount;
        return withdrawUser;
    }
}
