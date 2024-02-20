package com.foodielog.application.user.service.dto;

import lombok.Getter;

@Getter
public class ExistsNickNameResp {
	private final String nickName;
	private final Boolean isExists;

	public ExistsNickNameResp(String nickName, Boolean isExists) {
		this.nickName = nickName;
		this.isExists = isExists;
	}
}
