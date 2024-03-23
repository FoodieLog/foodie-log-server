package com.foodielog.application.reply.dto;

import com.foodielog.server.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ReplyCreateParam {

    private User user;

    private Long feedId;

    private String content;

    private Long parentId;

    private List<Long> mentionedIds;
}
