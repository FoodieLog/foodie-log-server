package com.foodielog.application.reply.dto;

import com.foodielog.server.user.entity.User;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
public class ReplyCreateReq {

    @NotBlank(message = "내용이 공백일 수 없습니다.")
    @Size(max = 150, message = "최대 글자수는 150 입니다.")
    private String content;

    @Positive
    private Long parentId;

    public ReplyCreateParam toParamWith(User user, Long feedId) {
        return ReplyCreateParam.builder()
                .user(user)
                .feedId(feedId)
                .content(content)
                .parentId(parentId)
                .build();
    }
}
