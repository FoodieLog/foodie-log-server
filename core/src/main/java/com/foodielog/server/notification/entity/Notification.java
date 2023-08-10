package com.foodielog.server.notification.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import com.foodielog.server.notification.type.NotificationType;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.type.Flag;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
