package com.foodielog.server._core.kakaoApi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoApiRequest {

    private String query = "";  // 검색을 원하는 문자열로서 UTF-8로 인코딩한다.
    private int size = 15;  // 한 페이지에 보여질 문서의 개수 (최소: 1, 최대: 45, 기본값: 15)
    private int  page = 1;  // 결과 페이지 번호(최소: 1, 최대: 45, 기본값: 1)
    private String category_group_code = "FD6, CE7";

    public MultiValueMap<String, String> toMultiValueMap() {
        var map = new LinkedMultiValueMap<String, String>();

        map.add("query", query);
        map.add("size", String.valueOf(size));
        map.add("page", String.valueOf(page));
        map.add("category_group_code", category_group_code);

        return map;
    }
}

