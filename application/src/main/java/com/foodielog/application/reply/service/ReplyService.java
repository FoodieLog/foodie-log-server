package com.foodielog.application.reply.service;

import com.foodielog.application._core.fcm.FcmMessageProvider;
import com.foodielog.application.feed.service.FeedModuleService;
import com.foodielog.application.notification.service.NotificationModuleService;
import com.foodielog.application.reply.dto.ReplyCreateParam;
import com.foodielog.application.reply.dto.ReportReplyParam;
import com.foodielog.application.reply.service.dto.ReplyCreateResp;
import com.foodielog.application.reply.service.dto.ReplyListResp;
import com.foodielog.application.report.service.ReportModuleService;
import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.notification.entity.Notification;
import com.foodielog.server.notification.type.NotificationType;
import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.report.entity.Report;
import com.foodielog.server.report.type.ReportType;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.type.Flag;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReplyService {

    private final FeedModuleService feedModuleService;
    private final ReplyModuleService replyModuleService;
    private final NotificationModuleService notificationModuleService;
    private final ReportModuleService reportModuleService;

    private final FcmMessageProvider fcmMessageProvider;

    @Transactional
    public ReplyCreateResp createReply(ReplyCreateParam parameter) {
        Feed feed = feedModuleService.get(parameter.getFeedId());
        User user = parameter.getUser();
        Reply reply = Reply.createReply(user, feed, parameter.getContent());

        // 자식 댓글인 경우 부모 update
        if (parameter.getParentId() != null) {
            Reply parent = replyModuleService.getNormal(parameter.getParentId());

            // 같은 게시글에 대한 댓글인지 체크
            if (!feed.equals(parent.getFeed())) {
                throw new Exception400("parentId", "부모 댓글과 게시물이 일치하지 않습니다.");
            }

            reply.updateParent(parent);
        }

        Reply saveReply = replyModuleService.save(reply);

        if (feed.getUser().getNotificationFlag() == Flag.Y) {
            Notification notification = Notification.createNotification(feed.getUser(),
                NotificationType.REPLY,
                saveReply.getId());
            notificationModuleService.save(notification);

            fcmMessageProvider.sendReplyMessage(feed.getUser().getEmail(), user.getEmail());
        }
        return new ReplyCreateResp(saveReply);
    }

    @Transactional
    public void deleteReply(User user, Long replyId) {
        Reply reply = replyModuleService.getUserReply(replyId, user.getId());

        reply.deleteReplyByUser();
    }

    @Transactional(readOnly = true)
    public ReplyListResp.ListDTO getReplys(Long feedId, Long last, Pageable pageable) {
        Feed feed = feedModuleService.get(feedId);

        List<Reply> replyList = replyModuleService.getFeedReplyPage(feedId, last, pageable);

        List<ReplyListResp.ReplyDTO> replyListDTO = replyList.stream()
            .map((Reply reply) -> {
                List<ReplyListResp.ReplyDTO> children = reply.getChildren().stream()
                    .map((Reply child) -> new ReplyListResp.ReplyDTO(child, new ArrayList<>()))
                    .collect(Collectors.toList());
                return new ReplyListResp.ReplyDTO(reply, children);
            })
            .collect(Collectors.toList());

        return new ReplyListResp.ListDTO(feed, replyListDTO);
    }

    @Transactional
    public void reportReply(ReportReplyParam parameter) {
        Reply reply = replyModuleService.getNormal(parameter.getReplyId());

        User reported = reply.getUser();
        User user = parameter.getUser();

        if (user.getId().equals(reported.getId())) {
            throw new Exception404("자신의 댓글은 신고 할 수 없습니다.");
        }

        reportModuleService.hasReportedByType(user, ReportType.REPLY, reply.getId());

        Report report = Report.createReport(user, reported, ReportType.REPLY, reply.getId(),
            parameter.getReportReason());
        reportModuleService.save(report);
    }

}
