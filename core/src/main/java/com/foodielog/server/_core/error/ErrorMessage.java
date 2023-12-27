package com.foodielog.server._core.error;

public class ErrorMessage {
    public static final String USER_NOT_FOUND = "유저가 존재하지 않습니다.";
    public static final String PASSWORD_NOT_MATCH = "비밀번호가 일치하지 않습니다.";

    public static final String FAIL_UPLOAD = "사진 전송에 실패하였습니다.";
    public static final String NO_SELECTED_IMAGE = "선택된 사진이 없습니다.";
    public static final String EXCEED_IMAGE_SIZE = "사진의 용량이 커서 업로드 할 수 없습니다.";
    public static final String NOT_IMAGE_EXTENSION = "사진만 업로드 가능합니다.";
    public static final String DUPLICATE_IMAGE = "동일한 사진은 업로드 할 수 없습니다.";
    public static final String FAIL_DELETE = "사진 삭제에 실패하였습니다.";
    public static final String NO_IMAGE_EXIST = "해당 사진이 존재하지 않습니다.";
}
