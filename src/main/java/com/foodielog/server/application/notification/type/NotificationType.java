package com.foodielog.server.application.notification.type;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum NotificationType {
	FEED("피드"),
	LIKE("좋아요"),
	FOLLOW("팔로우");

	private final String label;
}
