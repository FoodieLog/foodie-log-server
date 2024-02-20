package com.foodielog.application.user.service.dto;

import lombok.Getter;

@Getter
public class WithdrawResp {
	private final String email;
	private final Boolean success;

	public WithdrawResp(String email, Boolean success) {
		this.email = email;
		this.success = success;
	}
}