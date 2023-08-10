package com.foodielog.server._core.error.exception;

import com.foodielog.server._core.error.ErrorMessage;

public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(ErrorMessage.API_ERROR + message);
    }
}
