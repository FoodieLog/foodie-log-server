package com.foodielog.server.user;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@DynamicInsert@Table(name = "user_tb")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String provider;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String grade;
    private String nickName;
    private String profileImageUrl;
    private String aboutMe;
    private String badge;
    private String status;
    
    @CreationTimestamp
    private Timestamp createAt;
    private Timestamp updateAt;
}
