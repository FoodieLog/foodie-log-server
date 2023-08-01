package com.foodielog.server.user;

import com.foodielog.server.types.BlockReason;
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
@Table(name = "blockUser_tb")
@Entity
public class BlockUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private BlockReason reason;

    @Column(name = "feed_count")
    private Long feedCount;

    @Column(name = "reply_count")
    private Long replyCount;

    @CreationTimestamp
    private Timestamp createdAt;
}
