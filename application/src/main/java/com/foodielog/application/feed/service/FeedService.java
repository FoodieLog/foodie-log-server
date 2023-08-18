package com.foodielog.application.feed.service;

import com.foodielog.application.feed.dto.FeedRequest;
import com.foodielog.server._core.kakaoApi.KakaoApiResponse;
import com.foodielog.server._core.s3.S3Uploader;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.entity.Media;
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

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;
    private final RestaurantRepository restaurantRepository;
    private final MediaRepository mediaRepository;
    private final RestaurantLikeRepository restaurantLikeRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public void save(FeedRequest.SaveDTO saveDTO, List<MultipartFile> files, User user) {
        Restaurant restaurant = dtoToRestaurant(saveDTO.getSelectedSearchPlace());

        if (!isDuplicate(restaurant)) {
            restaurantRepository.save(restaurant);
        }
        checkIsLiked(user, restaurant, saveDTO);

        List<String> filesUrl = s3Uploader.saveFiles(files);

        Feed feed = Feed.createFeed(restaurant, user, saveDTO.getContent(), filesUrl.get(0));
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

    private boolean isDuplicate(Restaurant restaurant) {
        return restaurantRepository.findByKakaoPlaceId(restaurant.getKakaoPlaceId()).isPresent();
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
}
