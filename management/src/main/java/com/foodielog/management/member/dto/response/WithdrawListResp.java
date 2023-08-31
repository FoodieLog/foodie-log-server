package com.foodielog.management.member.dto.response;

import com.foodielog.server.admin.entity.WithdrawUser;
import com.foodielog.server.admin.type.WithdrawReason;
import com.foodielog.server.user.type.Flag;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;

@Getter
public class WithdrawListResp {
    private final List<WithDrawMemberDTO> content;

    public WithdrawListResp(List<WithDrawMemberDTO> content) {
        this.content = content;
    }

    @Getter
    public static class WithDrawMemberDTO {
        private final String nickName;
        private final String email;
        private final Flag flag;
        private final Long feedCount;
        private final Long replyCount;
        private final Timestamp createdAt;
        private final Timestamp withDrawAt;
        private final WithdrawReason withdrawReason;

        public WithDrawMemberDTO(WithdrawUser withdrawUser) {
            this.nickName = withdrawUser.getUser().getNickName();
            this.email = withdrawUser.getUser().getEmail();
            this.flag = withdrawUser.getUser().getBadgeFlag();
            this.feedCount = withdrawUser.getFeedCount();
            this.replyCount = withdrawUser.getReplyCount();
            this.createdAt = withdrawUser.getUser().getCreatedAt();
            this.withDrawAt = withdrawUser.getCreatedAt();
            this.withdrawReason = withdrawUser.getWithdrawReason();
        }
    }
}
