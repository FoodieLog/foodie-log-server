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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReplyService {
    
    private final FeedRepository feedRepository;
    private final ReplyRepository replyRepository;

    @Transactional
    public ReplyResponse.CreateDTO createReply(User user, Long feedId, ReplyRequest.CreateDTO createDTO) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new Exception404("에러"));

        Reply reply = Reply.createReply(user, feed, createDTO.getContent());
        Reply saveReply = replyRepository.save(reply);

        return new ReplyResponse.CreateDTO(saveReply);
    }

    @Transactional
    public void deleteReply(User user, Long replyId) {
        Reply reply = replyRepository.findByIdAndUserId(replyId, user.getId())
                .orElseThrow(() -> new Exception404("에러"));

        reply.deleteReply();
    }

    @Transactional(readOnly = true)
    public ReplyResponse.ListDTO getListReply(Long feedId, Long replyId, Pageable pageable) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new Exception404("에러"));

        List<Reply> replyList = replyRepository.getReplyList(feedId, replyId, pageable);

        List<ReplyResponse.ReplyDTO> replyListDTO = replyList.stream()
                .map(ReplyResponse.ReplyDTO::new)
                .collect(Collectors.toList());

        return new ReplyResponse.ListDTO(feed, replyListDTO);
    }
}
