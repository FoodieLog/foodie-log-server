package com.foodielog.server.user.type;

import com.foodielog.server._core.error.exception.Exception400;

public enum Flag {
    Y,
    N;

    public static Flag formString(String source) {
        try {
            Flag flag = Flag.valueOf(source.toUpperCase());
            return flag;
        } catch (IllegalArgumentException exception) {
            throw new Exception400(source, "잘못된 요청입니다.");
        }
    }
}
