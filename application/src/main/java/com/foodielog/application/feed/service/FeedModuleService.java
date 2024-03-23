package com.foodielog.application.feed.service;

import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.repository.FeedRepository;
import com.foodielog.server.feed.type.ContentStatus;
import com.foodielog.server.restaurant.entity.Restaurant;
import com.foodielog.server.restaurant.type.RestaurantCategory;
import com.foodielog.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

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

    public List<Feed> getRestaurantLatestFeeds(Restaurant restaurant) {
        return feedRepository.findAllByRestaurantIdAndStatusOrderByIdDesc(restaurant.getId(), ContentStatus.NORMAL);
    }

    public List<Feed> getRestaurantPopularFeeds(Restaurant restaurant) {
        return feedRepository.findPopularFeed(restaurant.getId());
    }

    public Feed save(Feed feed) {
        return feedRepository.save(feed);
    }

    public List<Feed> getMainFeeds(User user, Long lastFeed, RestaurantCategory category) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime date = now.minusMonths(1);
        Pageable pageable = PageRequest.of(0, 15, Sort.by(Sort.Direction.DESC, "id"));
        return feedRepository.getMainFeed(user, lastFeed, category, 0L, Timestamp.valueOf(date), pageable);
    }

    public List<Feed> getTopThree(Restaurant restaurant) {
        Pageable pageable = PageRequest.of(0, 3);
        return feedRepository.findTop3ByRestaurantId(restaurant.getId(), pageable);
    }
}
