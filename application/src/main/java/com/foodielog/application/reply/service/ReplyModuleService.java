package com.foodielog.application.reply.service;

import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.type.ContentStatus;
import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.reply.repository.ReplyRepository;
import com.foodielog.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public List<Reply> getFeedReplyPage(Long feedId, Long last, Pageable pageable) {
        return replyRepository.getReplyList(feedId, last, pageable);
    }

    public Reply save(Reply reply) {
        return replyRepository.save(reply);
    }

    public Reply getUserReply(Long replyId, Long userId) {
        return replyRepository.findByIdAndUserIdAndStatus(replyId, userId, ContentStatus.NORMAL)
                .orElseThrow(() -> new Exception404("해당 댓글이 없습니다."));
    }

    public Reply getNormal(Long replyId) {
        return replyRepository.findByIdAndStatus(replyId, ContentStatus.NORMAL)
                .orElseThrow(() -> new Exception404("해당 댓글이 없습니다."));
    }

    public Optional<Reply> getNormalOptional(Long replyId) {
        return replyRepository.findByIdAndStatus(replyId, ContentStatus.NORMAL);
    }

    public List<Reply> getNormalReplys(Long feedId) {
        return replyRepository.findByFeedIdAndStatus(feedId, ContentStatus.NORMAL);
    }

    public Long countReply(Feed feed) {
        return replyRepository.countByFeedAndStatus(feed, ContentStatus.NORMAL);
    }
}
