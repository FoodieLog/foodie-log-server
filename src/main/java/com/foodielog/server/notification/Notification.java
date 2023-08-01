package com.foodielog.server.notification;

import com.foodielog.server.types.Flag;
import com.foodielog.server.types.NotificationType;
import com.foodielog.server.user.User;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification_tb")
@Getter
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
