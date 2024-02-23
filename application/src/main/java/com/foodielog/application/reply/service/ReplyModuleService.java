package com.foodielog.application.reply.service;

import java.util.List;

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
}
