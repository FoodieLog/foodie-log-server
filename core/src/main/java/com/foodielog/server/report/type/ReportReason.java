package com.foodielog.server.report.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReportReason {
	ADVERTISEMENT("광고"),
	SWEARING("욕설"),
	DEFAMATION ("명예훼손"),
	OBSCENITY("음란"),
	ETC("기타")
	;

	private final String label;
}
