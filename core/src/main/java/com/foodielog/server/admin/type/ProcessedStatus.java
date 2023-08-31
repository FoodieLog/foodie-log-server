package com.foodielog.server.admin.type;

import com.foodielog.server._core.error.exception.Exception400;

public enum ProcessedStatus {
    UNPROCESSED,
    APPROVED,
    REJECTED;

    public static ProcessedStatus formString(String source) {
        try {
            ProcessedStatus status = ProcessedStatus.valueOf(source.toUpperCase());
            return status;
        } catch (IllegalArgumentException exception) {
            throw new Exception400(source, "잘못된 요청입니다.");
        }
    }
}
