package com.foodielog.application.user.service.dto;

import com.foodielog.server.user.entity.User;

import lombok.Getter;

@Getter
public class WithdrawResp {
	private final String email;
	private final Boolean success;

	public WithdrawResp(User user, Boolean success) {
		this.email = user.getEmail();
		this.success = success;
	}
}