package com.foodielog.server._core.error.exception;

import com.foodielog.server._core.util.ApiUtils;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class Exception500 extends RuntimeException {
    public Exception500(String message) {
        super(message);
    }


    public ApiUtils.ApiResult<?> body() {
        return ApiUtils.error(getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public HttpStatus status() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
