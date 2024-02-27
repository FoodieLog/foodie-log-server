package com.foodielog.application.reply.service;

import java.util.List;
import java.util.Optional;

import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server.feed.entity.Feed;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.foodielog.server.feed.type.ContentStatus;
import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.reply.repository.ReplyRepository;
import com.foodielog.server.user.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReplyModuleService {
	private final ReplyRepository replyRepository;

	public Long getUserCount(User user) {
		return replyRepository.countByUserAndStatus(user, ContentStatus.NORMAL);
	}

	public List<Reply> getUserReplys(User user) {
		return replyRepository.findByUserIdAndStatus(user.getId(), ContentStatus.NORMAL);
	}

	public List<Reply> getFeedReplys(Long feedId, Long lastReplyId, Pageable pageable) {
		return replyRepository.getReplyList(feedId, lastReplyId, pageable);
	}

	public Reply save(Reply reply) {
		return replyRepository.save(reply);
	}

	public Reply findByIdAndUserIdAndStatus(Long replyId, Long userId) {
		return replyRepository.findByIdAndUserIdAndStatus(replyId, userId, ContentStatus.NORMAL)
				.orElseThrow(() -> new Exception404("해당 댓글이 없습니다."));
	}

	public Reply findByIdAndStatus(Long replyId) {
		return replyRepository.findByIdAndStatus(replyId, ContentStatus.NORMAL)
				.orElseThrow(() -> new Exception404("해당 댓글이 없습니다."));
	}

	public Optional<Reply> findByIdAndStatusOp(Long replyId) {
		return replyRepository.findByIdAndStatus(replyId, ContentStatus.NORMAL);
	}

	public List<Reply> findByFeedIdAndStatus(Long feedId) {
		return replyRepository.findByFeedIdAndStatus(feedId, ContentStatus.NORMAL);
	}

	public Long countByFeedAndStatus(Feed feed) {
		return replyRepository.countByFeedAndStatus(feed, ContentStatus.NORMAL);
	}
}
