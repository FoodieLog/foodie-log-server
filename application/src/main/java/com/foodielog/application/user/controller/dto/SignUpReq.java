package com.foodielog.application.user.controller.dto;

import javax.validation.constraints.Email;

import org.springframework.web.multipart.MultipartFile;

import com.foodielog.server._core.customValid.valid.ValidNickName;
import com.foodielog.server._core.customValid.valid.ValidPassword;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SignUpReq {
	@Email
	private String email;

	@ValidPassword
	private String password;

	@ValidNickName
	private String nickName;

	private String aboutMe;

	public SignUpParam toParamWith(MultipartFile file) {
		return SignUpParam.builder()
			.email(email)
			.password(password)
			.nickName(nickName)
			.aboutMe(aboutMe)
			.file(file)
			.build();
	}
}