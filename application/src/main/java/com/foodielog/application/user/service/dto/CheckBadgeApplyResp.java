package com.foodielog.application.user.service.dto;

import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.type.Flag;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class CheckBadgeApplyResp {
	private final String nickName;
	private final Flag flag;
	private final Timestamp createdAt;

	public CheckBadgeApplyResp(User user, Timestamp createdAt) {
		this.nickName = user.getNickName();
		this.flag = createdAt != null ? Flag.Y : Flag.N;
		this.createdAt = createdAt;
	}
}
