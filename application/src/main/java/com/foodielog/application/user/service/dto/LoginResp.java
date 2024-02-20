package com.foodielog.application.user.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.type.Flag;

import lombok.Getter;

@Getter
public class LoginResp {
	private final Long id;
	private final String email;
	private final String nickName;
	private final String profileImageUrl;
	private final Flag notificationFlag;
	private final String accessToken;

	@JsonIgnore
	private final String refreshToken;

	public LoginResp(User user, String accessToken, String refreshToken) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.nickName = user.getNickName();
		this.profileImageUrl = user.getProfileImageUrl();
		this.notificationFlag = user.getNotificationFlag();
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
}
