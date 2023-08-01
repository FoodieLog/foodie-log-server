package com.foodielog.server.user;

import com.foodielog.server.types.Flag;
import com.foodielog.server.types.ProviderType;
import com.foodielog.server.types.Role;
import com.foodielog.server.types.UserStatus;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@DynamicInsert
@Table(name = "user_tb")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, length = 5)
    @Enumerated(EnumType.STRING)
    private ProviderType provider;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false, length = 50)
    private String nickName;

    @Column(length = 255)
    private String profileImageUrl;

    @Column(length = 150)
    private String aboutMe;

    @Column(nullable = false, length = 1)
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'Y'")
    private Flag notificationFlag;

    @Column(nullable = false, length = 1)
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'N'")
    private Flag badgeFlag;

    @Column(nullable = false, length = 5)
    private UserStatus status;
    
    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
