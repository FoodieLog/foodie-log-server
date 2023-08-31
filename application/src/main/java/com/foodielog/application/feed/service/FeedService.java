package com.foodielog.application.feed.service;

import com.foodielog.application.feed.dto.request.FeedSaveReq;
import com.foodielog.application.feed.dto.request.ReportFeedReq;
import com.foodielog.application.feed.dto.request.UpdateFeedReq;
import com.foodielog.application.feed.dto.response.FeedDetailResp;
import com.foodielog.application.feed.dto.response.MainFeedListResp;
import com.foodielog.server._core.error.exception.Exception403;
import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server._core.kakaoApi.KakaoApiResponse;
import com.foodielog.server._core.s3.S3Uploader;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.entity.FeedLike;
import com.foodielog.server.feed.entity.Media;
import com.foodielog.server.feed.repository.FeedLikeRepository;
import com.foodielog.server.feed.repository.FeedRepository;
import com.foodielog.server.feed.repository.MediaRepository;
import com.foodielog.server.feed.type.ContentStatus;
import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.reply.repository.ReplyRepository;
import com.foodielog.server.report.entity.Report;
import com.foodielog.server.report.repository.ReportRepository;
import com.foodielog.server.report.type.ReportType;
import com.foodielog.server.restaurant.entity.Restaurant;
import com.foodielog.server.restaurant.entity.RestaurantLike;
import com.foodielog.server.restaurant.repository.RestaurantLikeRepository;
import com.foodielog.server.restaurant.repository.RestaurantRepository;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FeedService {
    private final FeedRepository feedRepository;
    private final RestaurantRepository restaurantRepository;
    private final MediaRepository mediaRepository;
    private final RestaurantLikeRepository restaurantLikeRepository;
    private final S3Uploader s3Uploader;
    private final FeedLikeRepository feedLikeRepository;
    private final ReplyRepository replyRepository;
    private final ReportRepository reportRepository;
    private final FollowRepository followRepository;

    @Transactional
    public void save(FeedSaveReq request, List<MultipartFile> files, User user) {
        Restaurant restaurant = dtoToRestaurant(request.getSelectedSearchPlace());
        Restaurant savedRestaurant = saveRestaurant(restaurant);

        checkIsLiked(user, savedRestaurant, request);

        List<String> filesUrl = s3Uploader.saveFiles(files);

        Feed feed = Feed.createFeed(savedRestaurant, user, request.getContent(), filesUrl.get(0));
        feedRepository.save(feed);

        for (String fileUrl : filesUrl) {
            Media media = Media.createMedia(feed, fileUrl);
            mediaRepository.save(media);
        }
    }

    private void checkIsLiked(User user, Restaurant restaurant, FeedSaveReq request) {
        if (restaurantLikeRepository.existsByUserAndRestaurant(user, restaurant)) {
            return;
        }

        if (!request.getIsLiked()) {
            return;
        }

        RestaurantLike restaurantLike = RestaurantLike.createRestaurantLike(restaurant, user);
        restaurantLikeRepository.save(restaurantLike);
    }

    private Restaurant saveRestaurant(Restaurant restaurant) {
        Optional<Restaurant> existingRestaurant =
                restaurantRepository.findByKakaoPlaceId(restaurant.getKakaoPlaceId());

        return existingRestaurant.orElseGet(() -> restaurantRepository.save(restaurant));
    }

    private Restaurant dtoToRestaurant(KakaoApiResponse.SearchPlace searchPlace) {
        return Restaurant.createRestaurant(
                searchPlace.getPlace_name(),
                searchPlace.getId(),
                searchPlace.getPhone(),
                searchPlace.getCategory_name(),
                searchPlace.getPlace_url(),
                searchPlace.getX(),
                searchPlace.getY(),
                searchPlace.getAddress_name(),
                searchPlace.getRoad_address_name()
        );
    }

    @Transactional
    public void likeFeed(User user, Long feedId) {
        Feed feed = getFeed(feedId);

        Restaurant restaurant = feed.getRestaurant();
        likeRestaurantIfNotExists(user, restaurant);

        boolean isFeedLike = feedLikeRepository.existsByUserAndFeed(user, feed);

        if (isFeedLike) {
            throw new Exception404("이미 좋아요 된 피드입니다.");
        }

        FeedLike feedLike = FeedLike.createFeedLike(feed, user);
        feedLikeRepository.save(feedLike);
    }

    @Transactional
    public void unLikeFeed(User user, Long feedId) {
        Feed feed = getFeed(feedId);

        FeedLike feedLike = feedLikeRepository.findByUserAndFeed(user, feed)
                .orElseThrow(() -> new Exception404("좋아요 되지 않은 피드입니다."));

        feedLikeRepository.delete(feedLike);
    }

    private Feed getFeed(Long feedId) {
        return feedRepository.findByIdAndStatus(feedId, ContentStatus.NORMAL)
                .orElseThrow(() -> new Exception404("피드가 없습니다."));
    }

    private void likeRestaurantIfNotExists(User user, Restaurant restaurant) {
        boolean isRestaurantLike = restaurantLikeRepository.existsByUserAndRestaurant(user, restaurant);

        if (!isRestaurantLike) {
            RestaurantLike restaurantLike = RestaurantLike.createRestaurantLike(restaurant, user);
            restaurantLikeRepository.save(restaurantLike);
        }
    }

    @Transactional
    public void deleteFeed(User user, Long feedId) {
        Feed feed = getFeed(feedId);
        checkAccess(feed, user);

        List<Reply> replyList = replyRepository.findByFeedIdAndStatus(feedId, ContentStatus.NORMAL);
        replyList.forEach(Reply::deleteReplyByUser);

        feed.deleteFeedByUser();
    }

    @Transactional
    public void updateFeed(User user, UpdateFeedReq request) {
        Long feedId = request.getFeedId();
        Feed feed = getFeed(feedId);
        checkAccess(feed, user);

        String contents = request.getContent();
        feed.updateFeed(contents);
    }

    private void checkAccess(Feed feed, User user) {
        Long feedOwnerId = feed.getUser().getId();

        if (!feedOwnerId.equals(user.getId())) {
            throw new Exception403("해당 피드에 대한 권한이 없습니다.");
        }
    }

    @Transactional
    public void reportFeed(User user, ReportFeedReq request) {
        Feed feed = getFeed(request.getFeedId());
        User reported = feed.getUser();

        if (user.getId().equals(reported.getId())) {
            throw new Exception404("자신의 피드는 신고할 수 없습니다.");
        }

        checkReportedFeed(user, feed);

        Report report = Report.createReport(user, reported, ReportType.FEED, feed.getId(), request.getReportReason());
        reportRepository.save(report);
    }

    private void checkReportedFeed(User user, Feed feed) {
        boolean isReported = reportRepository.existsByReporterIdAndTypeAndContentId(user, ReportType.FEED, feed.getId());
        if (isReported) {
            throw new Exception404("이미 신고 처리된 피드입니다.");
        }
    }

    @Transactional(readOnly = true)
    public MainFeedListResp getMainFeed(User user, Long feedId, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime date = now.minusMonths(1);

        List<Feed> mainFeeds = feedRepository.getMainFeed(user, feedId, 0L, Timestamp.valueOf(date), pageable);

        List<MainFeedListResp.MainFeedsDTO> mainFeedDTOList = new ArrayList<>();

        for (Feed mainFeed : mainFeeds) {
            List<Media> mediaList = mediaRepository.findByFeed(mainFeed);

            List<MainFeedListResp.FeedImageDTO> feedImageDTO = getFeedImageDTO(mediaList);
            MainFeedListResp.FeedDTO feedDTO = getFeedDTO(mainFeed, feedImageDTO);
            MainFeedListResp.MainFeedRestaurantDTO mainFeedRestaurantDTO = getUserRestaurantDTO(mainFeed);

            boolean isFollowed = followRepository.existsByFollowedId(user);
            boolean isLiked = feedLikeRepository.existsByUser(user);

            mainFeedDTOList.add(new MainFeedListResp.MainFeedsDTO(feedDTO, mainFeedRestaurantDTO, isFollowed, isLiked));
        }
        return new MainFeedListResp(mainFeedDTOList);
    }

    private MainFeedListResp.MainFeedRestaurantDTO getUserRestaurantDTO(Feed feed) {
        Restaurant restaurant = feed.getRestaurant();
        return new MainFeedListResp.MainFeedRestaurantDTO(restaurant);
    }

    private MainFeedListResp.FeedDTO getFeedDTO(Feed feed, List<MainFeedListResp.FeedImageDTO> feedImages) {
        Long likeCount = feedLikeRepository.countByFeed(feed);
        Long replyCount = replyRepository.countByFeedAndStatus(feed, ContentStatus.NORMAL);

        return new MainFeedListResp.FeedDTO(feed, feedImages, likeCount, replyCount);
    }

    private List<MainFeedListResp.FeedImageDTO> getFeedImageDTO(List<Media> mediaList) {
        return mediaList.stream()
                .map(MainFeedListResp.FeedImageDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public FeedDetailResp getFeedDetail(Long feedId) {
        Feed feed = feedRepository.findByIdAndStatus(feedId, ContentStatus.NORMAL)
                .orElseThrow(() -> new Exception404("해당 피드를 찾을 수 없습니다."));

        List<Media> mediaList = mediaRepository.findByFeed(feed);
        List<FeedDetailResp.FeedImageDTO> feedImageDTOS = mediaList.stream()
                .map(FeedDetailResp.FeedImageDTO::new)
                .collect(Collectors.toList());

        Long likeCount = feedLikeRepository.countByFeed(feed);
        Long replyCount = replyRepository.countByFeedAndStatus(feed, ContentStatus.NORMAL);

        return new FeedDetailResp(feed, feedImageDTOS, likeCount, replyCount);
    }
}
