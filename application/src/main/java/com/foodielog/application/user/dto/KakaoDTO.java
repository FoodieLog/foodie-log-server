package com.foodielog.application.user.dto;

import lombok.Getter;

public class KakaoDTO {

	@Getter
	public static class Token {
		private String accessToken;
		private String tokenType;
		private String refreshToken;
		private int expiresIn;
		private String scope;
		private int refreshTokenExpiresIn;
	}

	@Getter
	public static class UserInfo {
		private Long id;
		private String connectedAt;
		private KakaoAccount kakaoAccount;
	}

	@Getter
	public static class KakaoAccount{
		private Boolean hasEmail;
		private Boolean emailNeedsAgreement; // 사용자 동의 여부
		private Boolean isEmailValid; // 이메일 유효 여부. false인 경우 일부 마스킹(Masking)하여 제공(예: ka***@kakao.com)
		private Boolean isEmailVerified; // 이메일 인증 여부. false인 경우 서비스에서 이메일이 올바르게 전송되지 않을 수 있음
		private String email;
	}
}
