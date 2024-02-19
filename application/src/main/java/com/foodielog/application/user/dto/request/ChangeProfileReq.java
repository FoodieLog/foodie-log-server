package com.foodielog.application.user.dto.request;

import org.springframework.web.multipart.MultipartFile;

import com.foodielog.server._core.customValid.valid.ValidNickName;
import com.foodielog.server.user.entity.User;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChangeProfileReq {
	@ValidNickName
	private String nickName;

	private String aboutMe;

	public ChangeProfileParam toParamWith(User user, MultipartFile file) {
		return ChangeProfileParam.builder()
			.user(user)
			.nickName(nickName)
			.aboutMe(aboutMe)
			.file(file)
			.build();
	}
}
