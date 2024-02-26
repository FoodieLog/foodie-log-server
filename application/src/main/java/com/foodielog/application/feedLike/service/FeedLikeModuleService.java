package com.foodielog.application.feedLike.service;

import com.foodielog.server.feed.entity.FeedLike;
import com.foodielog.server.feed.repository.FeedLikeRepository;
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
}
