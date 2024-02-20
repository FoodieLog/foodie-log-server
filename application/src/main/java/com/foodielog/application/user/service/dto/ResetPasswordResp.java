package com.foodielog.application.user.service.dto;

import lombok.Getter;

@Getter
public class ResetPasswordResp {
	private final String email;

	public ResetPasswordResp(String email) {
		this.email = email;
	}
}