package com.foodielog.server.user.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import com.foodielog.server.user.type.Flag;
import com.foodielog.server.user.type.ProviderType;
import com.foodielog.server.user.type.Role;
import com.foodielog.server.user.type.UserStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@DynamicInsert
@Table(name = "user_tb")
@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 255, unique = true)
	private String email;

	@Column(nullable = false, length = 60)
	private String password;

	@Column(nullable = false, length = 5)
	@Enumerated(EnumType.STRING)
	private ProviderType provider;

	@Column(nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	@ColumnDefault("'USER'")
	private Role role;

	@Column(nullable = false, length = 50, unique = true)
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

	@Column(nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	@ColumnDefault("'NORMAL'")
	private UserStatus status;

	@CreationTimestamp
	private Timestamp createdAt;

	@UpdateTimestamp
	private Timestamp updatedAt;
}
