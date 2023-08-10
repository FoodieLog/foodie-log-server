package com.foodielog.server._core.kakaoApi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoApiResponse {

    private Meta meta;
    private List<SearchPlace> documents;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        private SameName same_name;
        private int pageable_count;
        private int total_count;
        private boolean is_end;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SameName {
        private List<String> region;
        private String keyword;
        private String selected_region;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchPlace {
        private String place_name;
        //        private String distance;
        private String place_url;
        private String category_name;
        private String address_name;
        private String road_address_name;
        //        private String id;
//        private String phone;
//        private String category_group_code;
//        private String category_group_name;
        private String x;
        private String y;
    }
}
