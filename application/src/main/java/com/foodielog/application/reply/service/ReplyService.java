package com.foodielog.application.reply.service;

import com.foodielog.application._core.fcm.FcmMessageProvider;
import com.foodielog.application.reply.dto.ReplyCreateParam;
import com.foodielog.application.reply.dto.ReportReplyParam;
import com.foodielog.application.reply.service.dto.ReplyCreateResp;
import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.repository.FeedRepository;
import com.foodielog.server.feed.type.ContentStatus;
import com.foodielog.server.notification.entity.Notification;
import com.foodielog.server.notification.repository.NotificationRepository;
import com.foodielog.server.notification.type.NotificationType;
import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.reply.repository.ReplyRepository;
import com.foodielog.server.report.entity.Report;
import com.foodielog.server.report.repository.ReportRepository;
import com.foodielog.server.report.type.ReportType;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.type.Flag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReplyService {

    private final FeedRepository feedRepository;
    private final ReplyRepository replyRepository;
    private final ReportRepository reportRepository;
    private final NotificationRepository notificationRepository;

    private final FcmMessageProvider fcmMessageProvider;

    @Transactional
    public ReplyCreateResp createReply(ReplyCreateParam parameter) {
        Feed feed = getValidatedFeed(parameter.getFeedId());
        User user = parameter.getUser();

        Reply reply = Reply.createReply(user, feed, parameter.getContent());
        Reply saveReply = replyRepository.save(reply);

        if (feed.getUser().getNotificationFlag() == Flag.Y) {
            Notification notification = Notification.createNotification(feed.getUser(), NotificationType.REPLY, saveReply.getId());
            notificationRepository.save(notification);

            fcmMessageProvider.sendReplyMessage(feed.getUser().getEmail(), user.getEmail());
        }
        return new ReplyCreateResp(saveReply);
    }

    @Transactional
    public void deleteReply(User user, Long replyId) {
        Reply reply = replyRepository.findByIdAndUserIdAndStatus(replyId, user.getId(), ContentStatus.NORMAL)
                .orElseThrow(() -> new Exception404("해당 댓글이 없습니다."));

        reply.deleteReplyByUser();
    }

    @Transactional(readOnly = true)
    public ReplyCreateResp.ListDTO getListReply(Long feedId, Long replyId, Pageable pageable) {
        Feed feed = getValidatedFeed(feedId);

        List<Reply> replyList = replyRepository.getReplyList(feedId, replyId, pageable);

        List<ReplyCreateResp.ReplyDTO> replyListDTO = replyList.stream()
                .map(ReplyCreateResp.ReplyDTO::new)
                .collect(Collectors.toList());

        return new ReplyCreateResp.ListDTO(feed, replyListDTO);
    }

    @Transactional
    public void reportReply(ReportReplyParam parameter) {
        Reply reply = replyRepository.findByIdAndStatus(parameter.getReplyId(), ContentStatus.NORMAL)
                .orElseThrow(() -> new Exception404("해당 댓글이 없습니다."));

        User reported = reply.getUser();
        User user = parameter.getUser();

        if (user.getId().equals(reported.getId())) {
            throw new Exception404("자신의 댓글은 신고 할 수 없습니다.");
        }

        checkReportedReply(user, reply);

        Report report = Report.createReport(user, reported, ReportType.REPLY, reply.getId(), parameter.getReportReason());
        reportRepository.save(report);
    }

    private Feed getValidatedFeed(Long feedId) {
        return feedRepository.findByIdAndStatus(feedId, ContentStatus.NORMAL)
                .orElseThrow(() -> new Exception404("해당 피드가 없습니다."));
    }

    private void checkReportedReply(User user, Reply reply) {
        boolean isReported = reportRepository.existsByReporterIdAndTypeAndContentId(user, ReportType.REPLY, reply.getId());
        if (isReported) {
            throw new Exception404("이미 신고 처리된 댓글입니다.");
        }
    }
}
