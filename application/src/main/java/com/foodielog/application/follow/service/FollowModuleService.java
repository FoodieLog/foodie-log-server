package com.foodielog.application.follow.service;

import com.foodielog.server.user.entity.Follow;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FollowModuleService {
    private final FollowRepository followRepository;

    public Optional<Follow> getFollowById(Long id) {
        return followRepository.findById(id);
    }

    public boolean isFollow(User followingId, User followedId) {
        return followRepository.existsByFollowingIdAndFollowedId(followingId, followedId);
    }
}