package com.foodielog.server._core.util;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 공통 응답 DTO
public class ApiUtils {
	public static <T> ApiResult<T> success(T response, HttpStatus status) {
		return new ApiResult<>(status.value(), response, null);
	}

	public static <T> ApiResult<T> error(T message, HttpStatus status) {
		return new ApiResult<>(status.value(), null, new ApiError(message));
	}

	@Getter
	@AllArgsConstructor
	public static class ApiResult<T> {
		private final int status;
		private final T response;
		private final ApiError error;
	}

	@Getter
	@AllArgsConstructor
	public static class ApiError<T> {
		private final T message;
	}
}
