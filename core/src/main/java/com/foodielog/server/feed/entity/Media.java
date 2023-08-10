package com.foodielog.server.feed.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "media_tb")
@Entity
public class Media {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "feed_id")
	private Feed feed;

	@Column(name = "image_url", length = 80)
	private String imageUrl;

	@CreationTimestamp
	private Timestamp createdAt;

	public static Media createMedia(Feed feed, String imageUrl) {
		Media media = new Media();
		media.feed = feed;
		media.imageUrl = imageUrl;
		return media;
	}
}
