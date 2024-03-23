package com.foodielog.application.mention.service;

import com.foodielog.server.mention.entity.Mention;
import com.foodielog.server.mention.repository.MentionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MentionModuleService {
    private final MentionRepository mentionRepository;

    public Mention save(Mention mention) {
        return mentionRepository.save(mention);
    }

}
