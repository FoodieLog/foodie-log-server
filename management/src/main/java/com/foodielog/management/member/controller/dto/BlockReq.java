package com.foodielog.management.member.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Length;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BlockReq {

	@NotNull(message = "값이 null 일 수 없습니다.")
	@Positive(message = "양수만 가능합니다.")
	private Long userId;

	@Length(max = 100, message = "최대 100글자만 가능합니다.")
	@NotBlank(message = "차단 사유를 작성해주세요.")
	private String reason;

	public BlockProcessedParam toParam() {
		return BlockProcessedParam.builder()
			.userId(userId)
			.reason(reason)
			.build();
	}
}