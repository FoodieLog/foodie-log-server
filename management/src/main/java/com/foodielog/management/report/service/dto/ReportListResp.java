package com.foodielog.management.report.service.dto;

import com.foodielog.server.admin.type.ProcessedStatus;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.entity.Media;
import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.report.entity.Report;
import com.foodielog.server.report.type.ReportReason;
import com.foodielog.server.report.type.ReportType;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ReportListResp {
    private final List<ReportDTO<?>> content;

    public ReportListResp(List<ReportDTO<?>> content) {
        this.content = content;
    }

    @Getter
    public static class ReportDTO<T> {
        private final Long reportId;
        private final UserDTO reporter;
        private final UserDTO reported;
        private final ReportType type;
        private final T detail;
        private final ReportReason reason;
        private final ProcessedStatus status;
        private final Timestamp createdAt;
        private final Timestamp updatedAt;

        public ReportDTO(Report report, T detail) {
            this.reportId = report.getId();
            this.reporter = new UserDTO(report.getReporterId().getNickName(), report.getReporterId().getEmail());
            this.reported = new UserDTO(report.getReportedId().getNickName(), report.getReportedId().getEmail());
            this.type = report.getType();
            this.detail = detail;
            this.reason = report.getReportReason();
            this.status = report.getStatus();
            this.createdAt = report.getCreatedAt();
            this.updatedAt = report.getUpdatedAt();
        }
    }

    @Getter
    private static class UserDTO {
        private final String nickName;
        private final String email;

        private UserDTO(String nickName, String email) {
            this.nickName = nickName;
            this.email = email;
        }
    }

    @Getter
    public static class FeedDetail {
        private final Long feedId;
        private final List<FeedImageDTO> feedImages;
        private final String content;

        public FeedDetail(Feed feed, List<Media> mediaList) {
            this.feedId = feed.getId();
            this.feedImages = mediaList.stream()
                    .map(FeedImageDTO::new)
                    .collect(Collectors.toList());
            this.content = feed.getContent();
        }
    }

    @Getter
    private static class FeedImageDTO {
        private final String imageUrl;

        private FeedImageDTO(Media media) {
            this.imageUrl = media.getImageUrl();
        }
    }

    @Getter
    public static class ReplyDetail {
        private final Long id;
        private final String content;

        public ReplyDetail(Reply reply) {
            this.id = reply.getId();
            this.content = reply.getContent();
        }
    }
}
