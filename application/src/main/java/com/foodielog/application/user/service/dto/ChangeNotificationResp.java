package com.foodielog.application.user.service.dto;

import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.type.Flag;
import lombok.Getter;

@Getter
public class ChangeNotificationResp {
    private final String nickName;
    private final Flag replyFlag;
    private final Flag followFlag;
    private final Flag likeFlag;

    public ChangeNotificationResp(User user) {
        this.nickName = user.getNickName();
        this.replyFlag = user.getReplyFlag();
        this.followFlag = user.getFollowFlag();
        this.likeFlag = user.getLikeFlag();
    }
}
