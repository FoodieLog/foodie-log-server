package com.foodielog.server.application.feed;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;

import java.sql.Timestamp;

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
}
