package com.foodielog.application.user.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foodielog.server.user.entity.User;
import lombok.Getter;

@Getter
public class KakaoLoginResp {
    private final Long id;
    private final String nickName;
    private final String profileImageUrl;
    private final String accessToken;
    private final String kakaoAccessToken;

    @JsonIgnore
    private final String refreshToken;

    public KakaoLoginResp(User user, String accessToken, String refreshToken, String kakaoAccessToken) {
        this.id = user.getId();
        this.nickName = user.getNickName();
        this.profileImageUrl = user.getProfileImageUrl();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.kakaoAccessToken = kakaoAccessToken;
    }

    public static class kakaoApiResp {
        @Getter
        public static class UserInfo {
            private Long id;
            private String connectedAt;
            private KakaoAccount kakaoAccount;
        }

        @Getter
        public static class KakaoAccount {
            private Boolean hasEmail;
            private Boolean emailNeedsAgreement; // 사용자 동의 여부
            private Boolean isEmailValid; // 이메일 유효 여부. false인 경우 일부 마스킹(Masking)하여 제공(예: ka***@kakao.com)
            private Boolean isEmailVerified; // 이메일 인증 여부. false인 경우 서비스에서 이메일이 올바르게 전송되지 않을 수 있음
            private String email;
        }
    }
}

