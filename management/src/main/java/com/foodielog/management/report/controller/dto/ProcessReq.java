package com.foodielog.management.report.controller.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.foodielog.server._core.customValid.valid.ValidEnum;
import com.foodielog.server.admin.type.ProcessedStatus;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProcessReq {
	@NotNull(message = "값이 null 일 수 없습니다.")
	@Positive(message = "양수만 가능합니다.")
	private Long reportedId;

	@NotNull(message = "값이 null 일 수 없습니다.")
	@Positive(message = "양수만 가능합니다.")
	private Long contentId;

	@ValidEnum(enumClass = ProcessedStatus.class)
	private ProcessedStatus status;

	public ProcessParam toParam() {
		return ProcessParam.builder()
			.reportedId(reportedId)
			.contentId(contentId)
			.status(status)
			.build();
	}
}
