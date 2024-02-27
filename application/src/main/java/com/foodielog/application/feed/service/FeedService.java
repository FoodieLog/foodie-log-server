package com.foodielog.application.feed.service;

import com.foodielog.application._core.fcm.FcmMessageProvider;
import com.foodielog.application.feed.dto.FeedSaveParam;
import com.foodielog.application.feed.dto.LikeFeedParam;
import com.foodielog.application.feed.dto.ReportFeedParam;
import com.foodielog.application.feed.dto.UpdateFeedParam;
import com.foodielog.application.feed.service.dto.FeedDetailResp;
import com.foodielog.application.feed.service.dto.GetFeedResp;
import com.foodielog.application.feed.service.dto.MainFeedListResp;
import com.foodielog.application.feedLike.service.FeedLikeModuleService;
import com.foodielog.application.follow.service.FollowModuleService;
import com.foodielog.application.media.service.MediaModuleService;
import com.foodielog.application.notification.service.NotificationModuleService;
import com.foodielog.application.reply.service.ReplyModuleService;
import com.foodielog.application.report.service.ReportModuleService;
import com.foodielog.application.restaurant.service.RestaurantModuleService;
import com.foodielog.application.restaurantLike.service.RestaurantLikeModuleService;
import com.foodielog.server._core.error.exception.Exception403;
import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server._core.kakaoApi.KakaoApiResponse;
import com.foodielog.server._core.s3.S3Uploader;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.entity.FeedLike;
import com.foodielog.server.feed.entity.Media;
import com.foodielog.server.feed.repository.MediaRepository;
import com.foodielog.server.notification.entity.Notification;
import com.foodielog.server.notification.type.NotificationType;
import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.report.entity.Report;
import com.foodielog.server.report.repository.ReportRepository;
import com.foodielog.server.report.type.ReportType;
import com.foodielog.server.restaurant.entity.Restaurant;
import com.foodielog.server.restaurant.entity.RestaurantLike;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.type.Flag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FeedService {
    private final ReplyModuleService replyModuleService;
    private final NotificationModuleService notificationModuleService;
    private final FollowModuleService followModuleService;
    private final FeedLikeModuleService feedLikeModuleService;
    private final FeedModuleService feedModuleService;
    private final RestaurantModuleService restaurantModuleService;
    private final RestaurantLikeModuleService restaurantLikeModuleService;
    private final MediaModuleService mediaModuleService;
    private final ReportModuleService reportModuleService;

    private final FcmMessageProvider fcmMessageProvider;
    private final S3Uploader s3Uploader;

    @Transactional
    public void save(FeedSaveParam parameter) {
        Restaurant restaurant = dtoToRestaurant(parameter.getSelectedSearchPlace());
        Restaurant savedRestaurant = saveRestaurant(restaurant);
        User user = parameter.getUser();

        checkIsLiked(user, savedRestaurant, parameter.getIsLiked());

        List<String> filesUrl = s3Uploader.saveFiles(parameter.getFiles());

        Feed feed = Feed.createFeed(savedRestaurant, user, parameter.getContent(), filesUrl.get(0));
        feedModuleService.save(feed);

        for (String fileUrl : filesUrl) {
            Media media = Media.createMedia(feed, fileUrl);
            mediaModuleService.save(media);
        }
    }

    private void checkIsLiked(User user, Restaurant restaurant, boolean isLiked) {
        if (restaurantLikeModuleService.existsByUserAndRestaurant(user, restaurant)) {
            return;
        }

        if (!isLiked) {
            return;
        }

        RestaurantLike restaurantLike = RestaurantLike.createRestaurantLike(restaurant, user);
        restaurantLikeModuleService.save(restaurantLike);
    }

    private Restaurant saveRestaurant(Restaurant restaurant) {
        Optional<Restaurant> existingRestaurant =
                restaurantModuleService.getRestaurantByPlaceId(restaurant.getKakaoPlaceId());

        return existingRestaurant.orElseGet(() -> restaurantModuleService.save(restaurant));
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
    public void likeFeed(LikeFeedParam parameter) {
        Feed feed = getFeed(parameter.getFeedId());

        Restaurant restaurant = feed.getRestaurant();
        User user = parameter.getUser();
        likeRestaurantIfNotExists(user, restaurant);

        boolean isFeedLike = feedLikeModuleService.existsByUserAndFeed(user, feed);

        if (isFeedLike) {
            throw new Exception404("이미 좋아요 된 피드입니다.");
        }

        FeedLike feedLike = FeedLike.createFeedLike(feed, user);
        feedLikeModuleService.save(feedLike);

        if (feed.getUser().getNotificationFlag() == Flag.Y) {
            Notification notification = Notification.createNotification(feed.getUser(), NotificationType.LIKE, feedLike.getId());
            notificationModuleService.save(notification);

            fcmMessageProvider.sendLikeMessage(feed.getUser().getEmail(), user.getEmail());
        }
    }

    @Transactional
    public void unLikeFeed(User user, Long feedId) {
        Feed feed = getFeed(feedId);

        FeedLike feedLike = feedLikeModuleService.getFeedLikeByUserAndFeed(user, feed);

        feedLikeModuleService.delete(feedLike);
    }

    private Feed getFeed(Long feedId) {
        return feedModuleService.get(feedId);
    }

    private void likeRestaurantIfNotExists(User user, Restaurant restaurant) {
        boolean isRestaurantLike = restaurantLikeModuleService.existsByUserAndRestaurant(user, restaurant);

        if (!isRestaurantLike) {
            RestaurantLike restaurantLike = RestaurantLike.createRestaurantLike(restaurant, user);
            restaurantLikeModuleService.save(restaurantLike);
        }
    }

    @Transactional
    public void deleteFeed(User user, Long feedId) {
        Feed feed = getFeed(feedId);
        checkAccess(feed, user);

        List<Reply> replyList = replyModuleService.findByFeedIdAndStatus(feedId);
        replyList.forEach(Reply::deleteReplyByUser);

        feed.deleteFeedByUser();
    }

    @Transactional
    public void updateFeed(UpdateFeedParam parameter) {
        Feed feed = getFeed(parameter.getFeedId());
        checkAccess(feed, parameter.getUser());

        String contents = parameter.getContent();
        feed.updateFeed(contents);
    }

    private void checkAccess(Feed feed, User user) {
        Long feedOwnerId = feed.getUser().getId();

        if (!feedOwnerId.equals(user.getId())) {
            throw new Exception403("해당 피드에 대한 권한이 없습니다.");
        }
    }

    @Transactional
    public void reportFeed(ReportFeedParam parameter) {
        Feed feed = getFeed(parameter.getFeedId());
        User reported = feed.getUser();
        User user = parameter.getUser();

        if (user.getId().equals(reported.getId())) {
            throw new Exception404("자신의 피드는 신고할 수 없습니다.");
        }

        reportModuleService.existsByReporterIdAndTypeAndContentId(user, ReportType.FEED, feed.getId());

        Report report = Report.createReport(user, reported, ReportType.FEED, feed.getId(), parameter.getReportReason());
        reportModuleService.save(report);
    }

    @Transactional(readOnly = true)
    public MainFeedListResp getMainFeed(User user, Long feedId, Pageable pageable) {
        List<Feed> mainFeeds = feedModuleService.getMainFeed(user, feedId, pageable);

        List<MainFeedListResp.MainFeedsDTO> mainFeedDTOList = new ArrayList<>();

        for (Feed mainFeed : mainFeeds) {
            List<Media> mediaList = mediaModuleService.getMediaByFeed(mainFeed);

            List<MainFeedListResp.FeedImageDTO> feedImageDTO = getFeedImageDTO(mediaList);
            MainFeedListResp.FeedDTO feedDTO = getFeedDTO(mainFeed, feedImageDTO);
            MainFeedListResp.MainFeedRestaurantDTO mainFeedRestaurantDTO = getUserRestaurantDTO(mainFeed);

            boolean isFollowed = followModuleService.existsByFollowingIdAndFollowedId(user, mainFeed.getUser());
            boolean isLiked = feedLikeModuleService.existsByUserAndFeed(user, mainFeed);

            mainFeedDTOList.add(new MainFeedListResp.MainFeedsDTO(feedDTO, mainFeedRestaurantDTO, isFollowed, isLiked));
        }
        return new MainFeedListResp(mainFeedDTOList);
    }

    private MainFeedListResp.MainFeedRestaurantDTO getUserRestaurantDTO(Feed feed) {
        Restaurant restaurant = feed.getRestaurant();
        return new MainFeedListResp.MainFeedRestaurantDTO(restaurant);
    }

    private MainFeedListResp.FeedDTO getFeedDTO(Feed feed, List<MainFeedListResp.FeedImageDTO> feedImages) {
        Long likeCount = feedLikeModuleService.countByFeed(feed);
        Long replyCount = replyModuleService.countByFeedAndStatus(feed);

        return new MainFeedListResp.FeedDTO(feed, feedImages, likeCount, replyCount);
    }

    private List<MainFeedListResp.FeedImageDTO> getFeedImageDTO(List<Media> mediaList) {
        return mediaList.stream()
                .map(MainFeedListResp.FeedImageDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FeedDetailResp getFeedDetail(Long feedId) {
        Feed feed = getFeed(feedId);

        List<Media> mediaList = mediaModuleService.getMediaByFeed(feed);
        List<FeedDetailResp.FeedImageDTO> feedImageDTOS = mediaList.stream()
                .map(FeedDetailResp.FeedImageDTO::new)
                .collect(Collectors.toList());

        Long likeCount = feedLikeModuleService.countByFeed(feed);
        Long replyCount = replyModuleService.countByFeedAndStatus(feed);

        FeedDetailResp.RestaurantDTO restaurantDTO = new FeedDetailResp.RestaurantDTO(feed.getRestaurant());

        return new FeedDetailResp(feed, feedImageDTOS, restaurantDTO, likeCount, replyCount);
    }

    @Transactional(readOnly = true)
    public GetFeedResp getSingleFeed(User user, Long feedId) {
        Feed feed = getFeed(feedId);

        List<Media> mediaList = mediaModuleService.getMediaByFeed(feed);
        List<GetFeedResp.FeedImageDTO> feedImageDTOS = mediaList.stream()
                .map(GetFeedResp.FeedImageDTO::new)
                .collect(Collectors.toList());

        Long likeCount = feedLikeModuleService.countByFeed(feed);
        Long replyCount = replyModuleService.countByFeedAndStatus(feed);

        boolean isFollowed = followModuleService.existsByFollowingIdAndFollowedId(user, feed.getUser());
        boolean isLiked = feedLikeModuleService.existsByUserAndFeed(user, feed);

        GetFeedResp.FeedDTO feedDTO = new GetFeedResp.FeedDTO(feed, feedImageDTOS, likeCount, replyCount);

        GetFeedResp.RestaurantDTO restaurantDTO = new GetFeedResp.RestaurantDTO(feed.getRestaurant());
        GetFeedResp.GetFeedDTO getFeedDTO = new GetFeedResp.GetFeedDTO(feedDTO, restaurantDTO, isFollowed, isLiked);

        return new GetFeedResp(getFeedDTO);
    }
}
