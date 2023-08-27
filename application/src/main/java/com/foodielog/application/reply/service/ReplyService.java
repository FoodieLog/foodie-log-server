package com.foodielog.application.reply.service;

import com.foodielog.application.feed.dto.ReportFeedDTO;
import com.foodielog.application.reply.dto.ReplyCreatDTO;
import com.foodielog.application.reply.dto.ReportReplyDTO;
import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.repository.FeedRepository;
import com.foodielog.server.feed.type.ContentStatus;
import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.reply.repository.ReplyRepository;
import com.foodielog.server.report.entity.Report;
import com.foodielog.server.report.type.ReportType;
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
    public ReplyCreatDTO.Response createReply(User user, Long feedId, ReplyCreatDTO.Request createDTO) {
        Feed feed = feedRepository.findByIdAndStatus(feedId, ContentStatus.NORMAL)
                .orElseThrow(() -> new Exception404("에러"));

        Reply reply = Reply.createReply(user, feed, createDTO.getContent());
        Reply saveReply = replyRepository.save(reply);

        return new ReplyCreatDTO.Response(saveReply);
    }

    @Transactional
    public void deleteReply(User user, Long replyId) {
        Reply reply = replyRepository.findByIdAndUserIdAndStatus(replyId, user.getId(), ContentStatus.NORMAL)
                .orElseThrow(() -> new Exception404("에러"));

        reply.deleteReplyByUser();
    }

    @Transactional(readOnly = true)
    public ReplyCreatDTO.ListDTO getListReply(Long feedId, Long replyId, Pageable pageable) {
        Feed feed = feedRepository.findByIdAndStatus(feedId, ContentStatus.NORMAL)
                .orElseThrow(() -> new Exception404("에러"));

        List<Reply> replyList = replyRepository.getReplyList(feedId, replyId, pageable);

        List<ReplyCreatDTO.ReplyDTO> replyListDTO = replyList.stream()
                .map(ReplyCreatDTO.ReplyDTO::new)
                .collect(Collectors.toList());

        return new ReplyCreatDTO.ListDTO(feed, replyListDTO);
    }
}
