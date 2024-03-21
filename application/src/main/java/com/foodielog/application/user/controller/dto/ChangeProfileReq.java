package com.foodielog.application.user.controller.dto;

import com.foodielog.server._core.customValid.valid.ValidNickName;
import com.foodielog.server.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Getter
public class ChangeProfileReq {
    @ValidNickName
    private String nickName;

    private String aboutMe;

    public ChangeProfileParam toParamWith(User user, MultipartFile file) {
        return ChangeProfileParam.builder()
                .user(user)
                .nickName(nickName)
                .aboutMe(aboutMe)
                .file(file)
                .build();
    }
}
