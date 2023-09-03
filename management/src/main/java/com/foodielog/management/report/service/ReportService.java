package com.foodielog.management.report.service;

import com.foodielog.management.report.dto.request.ProcessReq;
import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server._core.smtp.MailService;
import com.foodielog.server.admin.entity.BlockUser;
import com.foodielog.server.admin.repository.BlockUserRepository;
import com.foodielog.server.admin.type.ProcessedStatus;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.repository.FeedRepository;
import com.foodielog.server.feed.type.ContentStatus;
import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.reply.repository.ReplyRepository;
import com.foodielog.server.report.entity.Report;
import com.foodielog.server.report.repository.ReportRepository;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.repository.UserRepository;
import com.foodielog.server.user.type.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReportService {
    private final MailService mailService;
    private final ReportRepository reportRepository;
    private final FeedRepository feedRepository;
    private final ReplyRepository replyRepository;
    private final BlockUserRepository blockUserRepository;
    private final UserRepository userRepository;

    @Transactional
    public void process(ProcessReq request) {
        User reportedUser = userRepository.findByIdAndStatus(request.getReportedId(), UserStatus.NORMAL)
                .orElseThrow(() -> new Exception404("해당 유저를 찾을 수 없습니다."));

        List<Report> reportList = reportRepository.findAllByReportedIdAndContentId(reportedUser, request.getContentId())
                .orElseThrow(() -> new Exception404("해당 신고를 찾을 수 없습니다."));

        switch (request.getStatus()) {
            case APPROVED:
                // 승인 처리
                reportList.forEach(Report::approve);

                // 이메일 전송
                sendProcessedMail(reportedUser, reportList);

                // 10 회 초과 시 자동 차단
                if (10L < reportRepository.countProcessedByStatus(reportedUser, ProcessedStatus.APPROVED)) {
                    blockUser(reportedUser);
                }

                break;
            case REJECTED:
                reportList.forEach(Report::reject);
                break;
            default:
                throw new Exception400(request.getStatus().name(), "잘못된 요청입니다.");
        }
    }

    private void sendProcessedMail(User reportedUser, List<Report> reportList) {
        mailService.sendEmail(reportedUser.getEmail(), "[Foodielog] 신고 승인 내역", "신고가 접수 되어 승인 되었습니다.");

        for (Report report : reportList) {
            User reporter = report.getReporterId();
            String email = reporter.getEmail();
            mailService.sendEmail(email, "[Foodielog] 신고 처리 완료", "접수하신 신고가 처리가 완료 되었습니다");
        }
    }

    private void blockUser(User user) {
        // 차단 유저 정보 저장
        Long feedCount = feedRepository.countByUserAndStatus(user, ContentStatus.NORMAL);
        Long replyCount = replyRepository.countByUserAndStatus(user, ContentStatus.NORMAL);

        BlockUser blockUser = BlockUser.createBlockByReport(user, feedCount, replyCount);
        blockUserRepository.save(blockUser);

        // 유저, 피드, 댓글 상태 변경
        user.block();
        userRepository.save(user);

        List<Feed> feedList = feedRepository.findByUserIdAndStatus(user.getId(), ContentStatus.NORMAL);
        feedList.forEach(Feed::deleteFeed);

        List<Reply> replyList = replyRepository.findByUserIdAndStatus(user.getId(), ContentStatus.NORMAL);
        replyList.forEach(Reply::deleteReply);
    }
}
