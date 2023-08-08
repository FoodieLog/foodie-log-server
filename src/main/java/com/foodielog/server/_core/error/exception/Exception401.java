package com.foodielog.server._core.error.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import com.foodielog.server._core.util.ApiUtils;


// 인증 안됨
@Getter
public class Exception401 extends RuntimeException {
    public Exception401(String message) {
        super(message);
    }

    public ApiUtils.ApiResult<?> body(){
        return ApiUtils.error(getMessage(), HttpStatus.UNAUTHORIZED);
    }

    public HttpStatus status(){
        return HttpStatus.UNAUTHORIZED;
    }
}