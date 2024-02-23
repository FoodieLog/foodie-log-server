package com.foodielog.application.user.service.dto;

import com.foodielog.server.user.entity.User;

import lombok.Getter;

@Getter
public class ChangePasswordResp {
	private final String password;

	public ChangePasswordResp(User user) {
		this.password = user.getEmail();
	}
}
