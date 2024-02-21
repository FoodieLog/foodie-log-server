package com.foodielog.management.user.controller.dto;

import javax.validation.constraints.Email;

import com.foodielog.server._core.customValid.valid.ValidPassword;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginReq {
	@Email
	private String email;

	@ValidPassword
	private String password;

	public LoginParam toParam() {
		return LoginParam.builder()
			.email(email)
			.password(password)
			.build();
	}
}
