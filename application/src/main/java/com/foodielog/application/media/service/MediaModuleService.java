package com.foodielog.application.media.service;

import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.entity.Media;
import com.foodielog.server.feed.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MediaModuleService {
    private final MediaRepository mediaRepository;

    public Media save(Media media) {
        return mediaRepository.save(media);
    }

    public List<Media> getMediaByFeed(Feed feed) {
        return mediaRepository.findByFeed(feed);
    }
}
