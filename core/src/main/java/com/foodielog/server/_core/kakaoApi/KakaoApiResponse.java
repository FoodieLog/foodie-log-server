package com.foodielog.server._core.kakaoApi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoApiResponse {

    private Meta meta;
    private List<SearchPlace> documents;

    @Getter
    public static class Meta {
        private int pageable_count;
        private int total_count;
        private boolean is_end;
    }

    @Getter
    public static class SearchPlace {
        private String place_name;
        private String place_url;
        private String category_name;
        private String address_name;
        private String road_address_name;
        private String phone;
        private String x;
        private String y;
    }
}
