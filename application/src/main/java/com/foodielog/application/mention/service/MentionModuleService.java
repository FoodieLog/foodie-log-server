package com.foodielog.application.mention.service;

import com.foodielog.server.mention.entity.Mention;
import com.foodielog.server.mention.repository.MentionRepository;
import com.foodielog.server.reply.entity.Reply;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MentionModuleService {
    private final MentionRepository mentionRepository;

    public Mention save(Mention mention) {
        return mentionRepository.save(mention);
    }

    public List<Mention> getAll(Reply reply) {
        return mentionRepository.findByReply(reply);
    }

    public Optional<Mention> getOptionalMention(Long id) {
        return mentionRepository.findById(id);
    }
}
