package com.foodielog.application.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.user.entity.User;

import lombok.Getter;

import java.util.List;

public class UserResponse {
	@Getter
	public static class LoginDTO {
		private String nickName;
		private String profileImageUrl;
		private String accessToken;

		@JsonIgnore
		private String refreshToken;

		public LoginDTO(User user, String accessToken, String refreshToken) {
			this.nickName = user.getNickName();
			this.profileImageUrl = user.getProfileImageUrl();
			this.accessToken = accessToken;
			this.refreshToken = refreshToken;
		}
	}

	@Getter
	public static class ProfileDTO {
		private final Long userId;
		private final String profileImageUrl;
		private final String aboutMe;
		private final Long feedCount;
		private final Long follower;
		private final Long following;

		public ProfileDTO(User user, Long feedCount, Long follower, Long following) {
			this.userId = user.getId();
			this.profileImageUrl = user.getProfileImageUrl();
			this.aboutMe = user.getAboutMe();
			this.feedCount = feedCount;
			this.follower = follower;
			this.following = following;
		}
	}

	@Getter
	public static class ThumbnailListDTO {
		private final List<ThumbnailDTO> content;

		public ThumbnailListDTO(List<ThumbnailDTO> content) {
			this.content = content;
		}
	}

	@Getter
	public static class ThumbnailDTO {
		private final Long id;
		private final String thumbnailUrl;

		public ThumbnailDTO(Feed feed) {
			this.id = feed.getId();
			this.thumbnailUrl = feed.getThumbnailUrl();
		}
	}
}
