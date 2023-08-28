package com.foodielog.application.reply.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class ReplyCreatReq {
    @NotBlank(message = "내용이 공백일 수 없습니다.")
    @Size(max = 150, message = "최대 글자수는 150 입니다.")
    private String content;
}
