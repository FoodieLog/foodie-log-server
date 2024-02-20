package com.foodielog.application.user.service.dto;

import lombok.Getter;

@Getter
public class SendCodeForSignupResp {
	private final String email;

	public SendCodeForSignupResp(String email) {
		this.email = email;
	}
}