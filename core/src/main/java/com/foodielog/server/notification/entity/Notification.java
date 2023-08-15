package com.foodielog.server.notification.entity;

import com.foodielog.server.notification.type.NotificationType;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.type.Flag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification_tb")
@Getter
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private Long contentId;

    @Column(nullable = false, length = 1)
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'N'")
    private Flag checkFlag;

    @CreationTimestamp
    private Timestamp createdAt;
}
