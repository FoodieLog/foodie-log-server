package com.foodielog.application.user.dto.response;

import lombok.Getter;

@Getter
public class ExistsEmailResp {
    private final String email;
    private final Boolean isExists;

    public ExistsEmailResp(String email, Boolean isExists) {
        this.email = email;
        this.isExists = isExists;
    }
}
