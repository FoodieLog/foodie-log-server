package com.foodielog.application.user.service.dto;

import lombok.Getter;

@Getter
public class ChangePasswordResp {
	private final String password;

	public ChangePasswordResp(String password) {
		this.password = password;
	}
}
