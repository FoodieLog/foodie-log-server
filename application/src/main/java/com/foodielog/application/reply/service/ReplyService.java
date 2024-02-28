package com.foodielog.application.reply.service;

import com.foodielog.application._core.fcm.FcmMessageProvider;
import com.foodielog.application.feed.service.FeedModuleService;
import com.foodielog.application.notification.service.NotificationModuleService;
import com.foodielog.application.reply.dto.ReplyCreateParam;
import com.foodielog.application.reply.dto.ReportReplyParam;
import com.foodielog.application.reply.service.dto.ReplyCreateResp;
import com.foodielog.application.report.service.ReportModuleService;
import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server.feed.entity.Feed;
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
		Reply saveReply = replyModuleService.save(reply);

		if (feed.getUser().getNotificationFlag() == Flag.Y) {
			Notification notification = Notification.createNotification(feed.getUser(), NotificationType.REPLY,
				saveReply.getId());
			notificationModuleService.save(notification);

			fcmMessageProvider.sendReplyMessage(feed.getUser().getEmail(), user.getEmail());
		}
		return new ReplyCreateResp(saveReply);
	}

	@Transactional
	public void deleteReply(User user, Long replyId) {
		Reply reply = replyModuleService.findByIdAndUserIdAndStatus(replyId, user.getId());

		reply.deleteReplyByUser();
	}

	@Transactional(readOnly = true)
	public ReplyCreateResp.ListDTO getReplys(Long feedId, Long lastReplyId, Pageable pageable) {
		Feed feed = feedModuleService.get(feedId);

		List<Reply> replyList = replyModuleService.getFeedReplys(feedId, lastReplyId, pageable);

		List<ReplyCreateResp.ReplyDTO> replyListDTO = replyList.stream()
			.map(ReplyCreateResp.ReplyDTO::new)
			.collect(Collectors.toList());

		return new ReplyCreateResp.ListDTO(feed, replyListDTO);
	}

	@Transactional
	public void reportReply(ReportReplyParam parameter) {
		Reply reply = replyModuleService.findByIdAndStatus(parameter.getReplyId());

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
