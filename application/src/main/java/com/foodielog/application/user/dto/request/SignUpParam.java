package com.foodielog.application.user.dto.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Getter
public class SignUpParam {
    private String email;

    private String password;

    private String nickName;

    private String aboutMe;

    MultipartFile file;
}
