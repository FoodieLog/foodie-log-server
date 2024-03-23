package com.foodielog.application.reply.service;

import com.foodielog.application._core.fcm.FcmMessageProvider;
import com.foodielog.application.feed.service.FeedModuleService;
import com.foodielog.application.mention.service.MentionModuleService;
import com.foodielog.application.notification.service.NotificationModuleService;
import com.foodielog.application.reply.dto.ReplyCreateParam;
import com.foodielog.application.reply.dto.ReportReplyParam;
import com.foodielog.application.reply.service.dto.ReplyCreateResp;
import com.foodielog.application.reply.service.dto.ReplyListResp;
import com.foodielog.application.report.service.ReportModuleService;
import com.foodielog.application.user.service.UserModuleService;
import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.mention.entity.Mention;
import com.foodielog.server.notification.entity.Notification;
import com.foodielog.server.notification.type.NotificationType;
import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.report.entity.Report;
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

    private final UserModuleService userModuleService;
    private final FeedModuleService feedModuleService;
    private final ReplyModuleService replyModuleService;
    private final NotificationModuleService notificationModuleService;
    private final MentionModuleService mentionModuleService;
    private final ReportModuleService reportModuleService;

    private final FcmMessageProvider fcmMessageProvider;

    @Transactional
    public ReplyCreateResp createReply(ReplyCreateParam parameter) {
        Feed feed = feedModuleService.get(parameter.getFeedId());
        User user = parameter.getUser();
        Reply savedReply = saveReply(parameter, user, feed);

        // 알림 처리
        Notification notification = Notification.createNotification(feed.getUser(),
                NotificationType.REPLY, savedReply.getId());
        notificationModuleService.save(notification);

        if (feed.getUser().getNotificationFlag() == Flag.Y) {
            fcmMessageProvider.sendReplyMessage(feed.getUser().getEmail(), user.getEmail());
        }

        // 멘션 있으면 멘션 처리
        saveMention(parameter.getMentionedIds(), savedReply);

        return new ReplyCreateResp(savedReply);
    }

    private Reply saveReply(ReplyCreateParam parameter, User user, Feed feed) {
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

        return replyModuleService.save(reply);
    }

    private void saveMention(List<Long> mentionedIds, Reply saveReply) {
        if (mentionedIds != null) {
            for (Long mentionedId : mentionedIds) {
                // 멘션 저장
                User mentionedUser = userModuleService.get(mentionedId);
                Mention mention = Mention.createMention(saveReply, mentionedUser);
                mentionModuleService.save(mention);

                // 알림 저장
                Notification notification = Notification.createNotification(
                        mentionedUser, NotificationType.MENTION, saveReply.getId());
                notificationModuleService.save(notification);
            }
        }
    }

    @Transactional
    public void deleteReply(User user, Long replyId) {
        Reply reply = replyModuleService.getUserReply(replyId, user.getId());

        reply.deleteReplyByUser();
    }

    @Transactional(readOnly = true)
    public ReplyListResp.ListDTO getReplys(Long feedId, Long last, Pageable pageable) {
        Feed feed = feedModuleService.get(feedId);

        List<Reply> replys = replyModuleService.getFeedReplyPage(feedId, last, pageable);

        List<ReplyListResp.ReplyDTO> replyListDTO = getReplyDTOS(replys);

        return new ReplyListResp.ListDTO(feed, replyListDTO);
    }

    private List<ReplyListResp.ReplyDTO> getReplyDTOS(List<Reply> replys) {
        return replys.stream()
                .map((Reply reply) -> {
                    List<Mention> mentions = mentionModuleService.getAll(reply);
                    List<ReplyListResp.MentionDTO> mentionDTOs = mentions.stream()
                            .map((Mention mention) -> new ReplyListResp.MentionDTO(
                                    mention.getMentionedUser().getId(), mention.getMentionedUser().getNickName()))
                            .collect(Collectors.toList());

                    List<Reply> children = reply.getChildren();
                    return new ReplyListResp.ReplyDTO(reply, getReplyDTOS(children), mentionDTOs);
                })
                .collect(Collectors.toList());
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
