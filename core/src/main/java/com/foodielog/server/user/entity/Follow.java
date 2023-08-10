package com.foodielog.server.user.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;

import com.foodielog.server.notification.entity.Notification;

public class Follow {
	@Id
	@Column(name = "following_id")
	private User followingId; // 나

	@Id
	@Column(name = "followed_id")
	private User followedId; // 너

	@ManyToOne
	@JoinColumn(name = "notification")
	private Notification notification;

	@CreationTimestamp
	private Timestamp createdAt; // 나 -> 너
}
