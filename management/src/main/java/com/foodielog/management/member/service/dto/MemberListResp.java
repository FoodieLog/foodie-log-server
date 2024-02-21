package com.foodielog.management.member.service.dto;

import java.sql.Timestamp;
import java.util.List;

import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.type.Flag;
import com.foodielog.server.user.type.UserStatus;

import lombok.Getter;

@Getter
public class MemberListResp {
	private final List<memberDTO> content;

	public MemberListResp(List<memberDTO> content) {
		this.content = content;
	}

	@Getter
	public static class memberDTO {
		private final Long userId;
		private final String nickName;
		private final String email;
		private final Flag flag;
		private final Long feedCount;
		private final Long replyCount;
		private final Timestamp createdAt;
		private final Long approveCount;
		private final UserStatus userStatus;

		public memberDTO(User user, Long feedCount, Long replyCount, Long approveCount) {
			this.userId = user.getId();
			this.nickName = user.getNickName();
			this.email = user.getEmail();
			this.flag = user.getBadgeFlag();
			this.feedCount = feedCount;
			this.replyCount = replyCount;
			this.createdAt = user.getCreatedAt();
			this.approveCount = approveCount;
			this.userStatus = user.getStatus();
		}
	}
}
