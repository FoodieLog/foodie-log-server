package com.foodielog.server._core.error.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import com.foodielog.server._core.util.ApiUtils;


// 권한 없음
@Getter
public class Exception404 extends RuntimeException {
    public Exception404(String message) {
        super(message);
    }

    public ApiUtils.ApiResult<?> body(){
        return ApiUtils.error(getMessage(), HttpStatus.NOT_FOUND);
    }

    public HttpStatus status(){
        return HttpStatus.NOT_FOUND;
    }
}