package com.foodielog.application.reply.service;

import com.foodielog.application.reply.dto.ReplyRequest;
import com.foodielog.application.reply.dto.ReplyResponse;
import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.repository.FeedRepository;
import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.reply.repository.ReplyRepository;
import com.foodielog.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReplyService {
    
    private final FeedRepository feedRepository;
    private final ReplyRepository replyRepository;

    @Transactional
    public ReplyResponse.createDTO createReply(User user, Long feedId, ReplyRequest.createDTO createDTO) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new Exception404("에러"));
        Reply reply = Reply.createReply(user, feed, createDTO.getContent());
        Reply saveReply = replyRepository.save(reply);
        return ReplyResponse.createDTO.from(saveReply);
    }

    @Transactional
    public void deleteReply(User user, Long replyId) {
        Reply reply = replyRepository.findByIdAndUserId(replyId, user.getId())
                .orElseThrow(() -> new Exception404("에러"));
        reply.deleteReply();
    }
}
