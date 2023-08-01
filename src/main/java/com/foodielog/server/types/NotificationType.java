package com.foodielog.server.types;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum NotificationType {
    FEED("피드"),
    LIKE("좋아요"),
    FOLLOW("팔로우");

    private final String label;
}
