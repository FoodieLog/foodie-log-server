package com.foodielog.management.member.service;

import com.foodielog.management.member.dto.request.BlockReq;
import com.foodielog.management.member.dto.response.BadgeApplyListResp;
import com.foodielog.management.member.dto.response.MemberListResp;
import com.foodielog.management.member.dto.response.WithdrawListResp;
import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server.admin.entity.BadgeApply;
import com.foodielog.server.admin.entity.BlockUser;
import com.foodielog.server.admin.entity.WithdrawUser;
import com.foodielog.server.admin.repository.BadgeApplyRepository;
import com.foodielog.server.admin.repository.BlockUserRepository;
import com.foodielog.server.admin.repository.WithdrawUserRepository;
import com.foodielog.server.admin.type.ProcessedStatus;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.repository.FeedRepository;
import com.foodielog.server.feed.type.ContentStatus;
import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.reply.repository.ReplyRepository;
import com.foodielog.server.report.repository.ReportRepository;
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
    private final ReportRepository reportRepository;
    private final BlockUserRepository blockUserRepository;

    @Transactional(readOnly = true)
    public WithdrawListResp getWithdrawList(String nickName, Flag badge, Pageable pageable) {
        List<WithdrawUser> withdrawUserList = withdrawUserRepository.findByFlag(pageable, nickName, badge);

        List<WithdrawListResp.WithdrawMemberDTO> withdrawMemberDTOS = withdrawUserList.stream()
                .map(WithdrawListResp.WithdrawMemberDTO::new)
                .collect(Collectors.toList());

        return new WithdrawListResp(withdrawMemberDTOS);
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

            Long feedCount = feedRepository.countByUserAndStatus(user, ContentStatus.NORMAL);
            Long replyCount = replyRepository.countByUserAndStatus(user, ContentStatus.NORMAL);
            Long followerCount = followRepository.countByFollowedId(user);

            BadgeApplyListResp.BadgeApplyMemberDTO badgeApplyMemberDTO = new BadgeApplyListResp.BadgeApplyMemberDTO(badgeApply, feedCount, replyCount, followerCount);
            badgeApplyMemberDTOS.add(badgeApplyMemberDTO);
        }

        return new BadgeApplyListResp(badgeApplyMemberDTOS);
    }

    @Transactional
    public void badgeProcessed(Long badgeApplyId, String process) {
        BadgeApply badgeApply = badgeApplyRepository.findByIdAndStatus(badgeApplyId, ProcessedStatus.UNPROCESSED)
                .orElseThrow(() -> new Exception404("해당 신청이 존재하지 않습니다."));

        User user = badgeApply.getUser();
        validateUserStatus(user);

        switch (process) {
            case "rejected":
                badgeApply.rejectBadge();
                break;
            case "approved":
                badgeApply.approveBadge();
                user.badgeApproved();
                break;
            default:
                throw new Exception400(process, "잘못된 요청값입니다.");
        }
    }

    private void validateUserStatus(User user) {
        if (user.getStatus() != UserStatus.NORMAL) {
            throw new Exception404("해당 회원의 뱃지를 처리할 수 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public MemberListResp getMemberList(String nickName, Flag badge, UserStatus userStatus, Pageable pageable) {
        List<User> memberList = userRepository.findAllByFlagAndStatus(nickName, badge, userStatus, pageable);

        List<MemberListResp.memberDTO> memberDTOList = new ArrayList<>();

        for (User member : memberList) {
            Long feedCount = feedRepository.countByUserAndStatus(member, ContentStatus.NORMAL);
            Long replyCount = replyRepository.countByUserAndStatus(member, ContentStatus.NORMAL);

            long approveCount = reportRepository.countProcessedByStatus(member, ProcessedStatus.APPROVED);

            MemberListResp.memberDTO memberDTO = new MemberListResp.memberDTO(member, feedCount, replyCount, approveCount);
            memberDTOList.add(memberDTO);
        }

        return new MemberListResp(memberDTOList);
    }

    @Transactional
    public void blockProcessed(BlockReq request) {
        User user = userRepository.findByIdAndStatus(request.getUserId(), UserStatus.NORMAL)
                .orElseThrow(() -> new Exception404("해당 회원이 존재 하지 않습니다."));

        Long feedCount = feedRepository.countByUserAndStatus(user, ContentStatus.NORMAL);
        Long replyCount = replyRepository.countByUserAndStatus(user, ContentStatus.NORMAL);

        BlockUser blockUser = BlockUser.createBlock(user, request.getReason(), feedCount, replyCount);

        user.block();

        blockUserRepository.save(blockUser);
    }
}
