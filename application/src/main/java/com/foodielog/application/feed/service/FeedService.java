package com.foodielog.application.feed.service;

import com.foodielog.application.feed.dto.FeedRequest;
import com.foodielog.server._core.kakaoApi.KakaoAPI;
import com.foodielog.server._core.kakaoApi.KakaoApiRequest;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final KakaoAPI kakaoAPI;
    private final FeedRepository feedRepository;
    private final RestaurantRepository restaurantRepository;
    private final MediaRepository mediaRepository;
    private final RestaurantLikeRepository restaurantLikeRepository;
    private final S3Uploader s3Uploader;

    @Transactional(readOnly = true)
    public KakaoApiResponse getSearch(String keyword) {
        KakaoApiRequest kakaoApiRequest = new KakaoApiRequest();
        kakaoApiRequest.setQuery(keyword);

        KakaoApiResponse kakaoApiResponse = kakaoAPI.searchRestaurantsByKeyword(kakaoApiRequest);

        if (kakaoApiResponse == null || kakaoApiResponse.getDocuments() == null) {
            // 추후 수정해야할듯?
            return new KakaoApiResponse(new KakaoApiResponse.Meta(), Collections.emptyList());
        }

        return kakaoApiResponse;
    }

    public void save(FeedRequest.SaveDTO saveDTO, List<MultipartFile> files, User user) throws IOException {
        List<String> storedFileNames = new ArrayList<>();

        for (MultipartFile file : files) {
            String storedFileName = s3Uploader.saveFile(file);
            storedFileNames.add(storedFileName);
        }

        Restaurant restaurant = dtoToRestaurant(saveDTO.getSelectedSearchPlace());
        restaurantRepository.save(restaurant);

        if (saveDTO.isLiked()){
            RestaurantLike restaurantLike = RestaurantLike.createRestaurantLike(restaurant, user);
            restaurantLikeRepository.save(restaurantLike);
        }

        Feed feed = Feed.createFeed(restaurant, user, saveDTO.getContent(), storedFileNames.get(0));
        feedRepository.save(feed);

        for (String storedFileName : storedFileNames) {
            Media media = Media.createMedia(feed, storedFileName);
            mediaRepository.save(media);
        }
    }

    private Restaurant dtoToRestaurant(KakaoApiResponse.SearchPlace searchPlace) {
        return Restaurant.createRestaurant(
                searchPlace.getPlace_name(),
                searchPlace.getCategory_name(),
                searchPlace.getPlace_url(),
                searchPlace.getX(),
                searchPlace.getY(),
                searchPlace.getAddress_name(),
                searchPlace.getRoad_address_name()
        );
    }
}
