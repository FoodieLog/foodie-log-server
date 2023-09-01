package com.foodielog.management.member.service;

import com.foodielog.management.member.dto.response.BadgeApplyListResp;
import com.foodielog.management.member.dto.response.WithdrawListResp;
import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server.admin.entity.BadgeApply;
import com.foodielog.server.admin.entity.WithdrawUser;
import com.foodielog.server.admin.repository.BadgeApplyRepository;
import com.foodielog.server.admin.repository.WithdrawUserRepository;
import com.foodielog.server.admin.type.ProcessedStatus;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.repository.FeedRepository;
import com.foodielog.server.feed.type.ContentStatus;
import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.reply.repository.ReplyRepository;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.repository.FollowRepository;
import com.foodielog.server.user.repository.UserRepository;
import com.foodielog.server.user.type.Flag;
import com.foodielog.server.user.type.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final WithdrawUserRepository withdrawUserRepository;
    private final BadgeApplyRepository badgeApplyRepository;
    private final FeedRepository feedRepository;
    private final ReplyRepository replyRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public WithdrawListResp getWithdrawList(String nickName, Flag badge, Pageable pageable) {
        List<WithdrawUser> withdrawUserList = withdrawUserRepository.findByFlag(pageable, nickName, badge);

        List<WithdrawListResp.WithDrawMemberDTO> withDrawMemberDTOS = withdrawUserList.stream()
                .map(WithdrawListResp.WithDrawMemberDTO::new)
                .collect(Collectors.toList());

        return new WithdrawListResp(withDrawMemberDTOS);
    }

    @Transactional
    public void restoreMember(Long withdrawId) {
        WithdrawUser withdrawUser = withdrawUserRepository.findById(withdrawId)
                .orElseThrow(() -> new Exception404("해당 회원을 찾을 수 없습니다."));

        User user = withdrawUser.getUser();
        user.restore();
        withdrawUserRepository.delete(withdrawUser);

        List<Feed> feedList = feedRepository.findByUserIdAndStatus(user.getId(), ContentStatus.DELETED);
        feedList.forEach(Feed::restoreFeed);

        List<Reply> replyList = replyRepository.findByUserIdAndStatus(user.getId(), ContentStatus.DELETED);
        replyList.forEach(Reply::restoreReply);
    }

    @Transactional(readOnly = true)
    public BadgeApplyListResp getBadgeApplyList(String nickName, ProcessedStatus processedStatus, Pageable pageable) {
        List<BadgeApply> badgeApplyList = badgeApplyRepository.findByStatus(pageable, nickName, processedStatus);

        List<BadgeApplyListResp.BadgeApplyMemberDTO> badgeApplyMemberDTOS = new ArrayList<>();

        for (BadgeApply badgeApply : badgeApplyList) {
            User user = badgeApply.getUser();

            Long feedCount = feedRepository.countByUser(user);
            Long replyCount = replyRepository.countByUser(user);
            Long followerCount = followRepository.countByFollowingId(user);

            BadgeApplyListResp.BadgeApplyMemberDTO badgeApplyMemberDTO = new BadgeApplyListResp.BadgeApplyMemberDTO(badgeApply, feedCount, replyCount, followerCount);
            badgeApplyMemberDTOS.add(badgeApplyMemberDTO);
        }

        return new BadgeApplyListResp(badgeApplyMemberDTOS);
    }

    @Transactional
    public void badgeProcessed(Long badgeApplyId, String process) {
        BadgeApply badgeApply = badgeApplyRepository.findByIdAndStatus(badgeApplyId, ProcessedStatus.UNPROCESSED)
                .orElseThrow(() -> new Exception404("해당 신청이 존재하지 않습니다."));

        switch (process) {
            case "rejected":
                badgeApply.rejectBadge();
                break;
            case "approved":
                badgeApply.approveBadge();
                Long userId = badgeApply.getUser().getId();
                User user = userRepository.findByIdAndStatus(userId, UserStatus.NORMAL)
                        .orElseThrow(() -> new Exception404("해당 회원이 존재하지 않습니다."));
                user.badgeApproved();
                break;
            default:
                throw new Exception400(process, "잘못된 요청값입니다.");
        }
    }
}
