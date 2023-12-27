package com.foodielog.server.admin.entity;

import com.foodielog.server.admin.type.WithdrawReason;
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

    private Long feedCount;

    private Long replyCount;

    @Enumerated(EnumType.STRING)
    private WithdrawReason withdrawReason;

    @CreationTimestamp
    private Timestamp createdAt;

    public static WithdrawUser createWithdrawUser(User user, Long feedCount, Long replyCount, WithdrawReason withdrawReason) {
        WithdrawUser withdrawUser = new WithdrawUser();
        withdrawUser.user = user;
        withdrawUser.feedCount = feedCount;
        withdrawUser.replyCount = replyCount;
        withdrawUser.withdrawReason = withdrawReason;
        return withdrawUser;
    }
}
