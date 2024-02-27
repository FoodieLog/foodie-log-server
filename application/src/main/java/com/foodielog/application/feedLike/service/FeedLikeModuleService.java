package com.foodielog.application.feedLike.service;

import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.entity.FeedLike;
import com.foodielog.server.feed.repository.FeedLikeRepository;
import com.foodielog.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FeedLikeModuleService {
    private final FeedLikeRepository feedLikeRepository;


    public Optional<FeedLike> getFeedLikeById(Long id) {
        return feedLikeRepository.findById(id);
    }

    public boolean existsByUserAndFeed(User user, Feed feed) {
        return feedLikeRepository.existsByUserAndFeed(user, feed);
    }

    public FeedLike getFeedLikeByUserAndFeed(User user, Feed feed) {
        return feedLikeRepository.findByUserAndFeed(user, feed)
                .orElseThrow(() -> new Exception404("좋아요 되지 않은 피드입니다."));
    }

    public FeedLike save(FeedLike feedLike) {
        return feedLikeRepository.save(feedLike);
    }

    public void delete(FeedLike feedLike) {
        feedLikeRepository.delete(feedLike);
    }

    public Long countByFeed(Feed feed) {
        return feedLikeRepository.countByFeed(feed);
    }
}
