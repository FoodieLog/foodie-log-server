package com.foodielog.application.user.service.dto;

import com.foodielog.server.user.entity.User;

import lombok.Getter;

import java.util.List;

@Getter
public class UserSearchResp {
	private final List<UserSearchResp.UserDTO> content;

	public UserSearchResp(List<UserSearchResp.UserDTO> content) {
		this.content = content;
	}

	@Getter
	public static class UserDTO {
		private final Long id;
		private final String nickName;
		private final String profileImageUrl;
		private final String aboutMe;

		public UserDTO(User user) {
			this.id = user.getId();
			this.nickName = user.getNickName();
			this.profileImageUrl = user.getProfileImageUrl();
			this.aboutMe = user.getAboutMe();
		}
	}
}