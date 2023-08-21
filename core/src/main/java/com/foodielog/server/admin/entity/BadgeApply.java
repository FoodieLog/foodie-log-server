package com.foodielog.server.admin.entity;

import com.foodielog.server.admin.type.ProcessedStatus;
import com.foodielog.server.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "badge_apply_tb")
@Entity
public class BadgeApply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'UNPROCESSED'")
    private ProcessedStatus status;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public static BadgeApply createBadgeApply(User user) {
        BadgeApply badgeApply = new BadgeApply();
        badgeApply.user = user;

        return badgeApply;
    }
}
