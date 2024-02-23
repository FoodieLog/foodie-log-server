package com.foodielog.application.feed.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.repository.FeedRepository;
import com.foodielog.server.feed.type.ContentStatus;
import com.foodielog.server.user.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FeedModuleService {
	private final FeedRepository feedRepository;

	public Feed get(Long id) {
		return feedRepository.findByIdAndStatus(id, ContentStatus.NORMAL)
			.orElseThrow(() -> new Exception404("해당 피드가 없습니다."));
	}

	public Long getUserCount(User user) {
		return feedRepository.countByUserAndStatus(user, ContentStatus.NORMAL);
	}

	public List<Feed> getUserFeeds(User user) {
		return feedRepository.findByUserIdAndStatus(user.getId(), ContentStatus.NORMAL);
	}
}
