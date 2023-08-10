package com.foodielog.server.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foodielog.server.user.entity.User;

import lombok.Getter;

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
}
