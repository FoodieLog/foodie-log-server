package com.foodielog.application.user.dto.request;

import com.foodielog.server.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Getter
public class ChangeProfileParam {
    private User user;

    private String nickName;

    private String aboutMe;

    private MultipartFile file;
}
