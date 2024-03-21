package com.foodielog.application.user.service.dto;

import com.foodielog.server.user.type.UserStatus;
import lombok.Getter;

@Getter
public class ExistsEmailResp {

    private final String email;
    private final Boolean isExists;
    private final UserStatus status;

    public ExistsEmailResp(String email, Boolean isExists, UserStatus status) {
        this.email = email;
        this.isExists = isExists;
        this.status = status;
    }
}
