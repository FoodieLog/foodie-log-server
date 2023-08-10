package com.foodielog.application.user.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;

public class UserRequest {

	@Getter
	public static class LoginDTO {
		@Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "이메일 형식이 올바르지 않습니다.")
		@NotEmpty
		private String email;

		@Size(max = 60)
		@NotEmpty
		private String password;
	}
}
