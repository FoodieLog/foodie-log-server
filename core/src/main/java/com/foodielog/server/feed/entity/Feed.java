package com.foodielog.server.feed.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import com.foodielog.server.feed.type.ContentStatus;
import com.foodielog.server.restaurant.entity.Restaurant;
import com.foodielog.server.user.entity.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@DynamicInsert
@Table(name = "feed_tb")
@Entity
public class Feed {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "restaurant_id")
	private Restaurant restaurant;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(nullable = false, length = 255)
	private String thumbnailUrl;

	@Column(nullable = false, length = 2000)
	private String content;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 7)
	private ContentStatus status;

	@CreationTimestamp
	private Timestamp createdAt;

	@UpdateTimestamp
	private Timestamp updatedAt;

	public static Feed createFeed(Restaurant restaurant, User user, String content, String thumbnailUrl) {
		Feed feed = new Feed();
		feed.restaurant = restaurant;
		feed.user = user;
		feed.content = content;
		feed.status = ContentStatus.NORMAL;
		feed.thumbnailUrl = thumbnailUrl;
		return feed;
	}
}
