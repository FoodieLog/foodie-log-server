package com.foodielog.application.user.dto.request;

import com.foodielog.server._core.customValid.valid.ValidPassword;
import com.foodielog.server.user.entity.User;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChangePasswordReq {
	@ValidPassword
	private String oldPassword;

	@ValidPassword
	private String newPassword;

	public ChangePasswordParam toParamWith(User user) {
		return ChangePasswordParam.builder().
			user(user)
			.oldPassword(oldPassword)
			.newPassword(newPassword)
			.build();
	}
}

