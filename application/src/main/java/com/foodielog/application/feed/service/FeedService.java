package com.foodielog.application.feed.service;

import com.foodielog.application.feed.dto.FeedRequest;
import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server._core.kakaoApi.KakaoApiResponse;
import com.foodielog.server._core.s3.S3Uploader;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.entity.FeedLike;
import com.foodielog.server.feed.entity.Media;
import com.foodielog.server.feed.repository.FeedLikeRepository;
import com.foodielog.server.feed.repository.FeedRepository;
import com.foodielog.server.feed.repository.MediaRepository;
import com.foodielog.server.restaurant.entity.Restaurant;
import com.foodielog.server.restaurant.entity.RestaurantLike;
import com.foodielog.server.restaurant.repository.RestaurantLikeRepository;
import com.foodielog.server.restaurant.repository.RestaurantRepository;
import com.foodielog.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;
    private final RestaurantRepository restaurantRepository;
    private final MediaRepository mediaRepository;
    private final RestaurantLikeRepository restaurantLikeRepository;
    private final S3Uploader s3Uploader;
    private final FeedLikeRepository feedLikeRepository;

    @Transactional
    public void save(FeedRequest.SaveDTO saveDTO, List<MultipartFile> files, User user) {
        Restaurant restaurant = dtoToRestaurant(saveDTO.getSelectedSearchPlace());
        Restaurant savedRestaurant = saveRestaurant(restaurant);

        checkIsLiked(user, savedRestaurant, saveDTO);

        List<String> filesUrl = s3Uploader.saveFiles(files);

        Feed feed = Feed.createFeed(savedRestaurant, user, saveDTO.getContent(), filesUrl.get(0));
        feedRepository.save(feed);

        for (String fileUrl : filesUrl) {
            Media media = Media.createMedia(feed, fileUrl);
            mediaRepository.save(media);
        }
    }

    private void checkIsLiked(User user, Restaurant restaurant, FeedRequest.SaveDTO saveDTO) {
        if (restaurantLikeRepository.existsByUserAndRestaurant(user, restaurant)) {
            return;
        }

        if (!saveDTO.getIsLiked()) {
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
        return feedRepository.findById(feedId)
                .orElseThrow(() -> new Exception404("피드가 없습니다."));
    }
}
