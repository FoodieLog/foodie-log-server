package com.foodielog.application.user.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodielog.application.user.controller.dto.LoginParam;
import com.foodielog.application.user.controller.dto.ResetPasswordParam;
import com.foodielog.application.user.controller.dto.SignUpParam;
import com.foodielog.application.user.service.dto.ExistsEmailResp;
import com.foodielog.application.user.service.dto.ExistsNickNameResp;
import com.foodielog.application.user.service.dto.LoginResp;
import com.foodielog.application.user.service.dto.ReissueResp;
import com.foodielog.application.user.service.dto.ResetPasswordResp;
import com.foodielog.application.user.service.dto.SendCodeForPasswordResp;
import com.foodielog.application.user.service.dto.SendCodeForSignupResp;
import com.foodielog.application.user.service.dto.SignUpResp;
import com.foodielog.application.user.service.dto.VerifiedCodeResp;
import com.foodielog.server._core.error.ErrorMessage;
import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server._core.redis.RedisService;
import com.foodielog.server._core.s3.S3Uploader;
import com.foodielog.server._core.security.jwt.JwtTokenProvider;
import com.foodielog.server._core.smtp.MailService;
import com.foodielog.server.user.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserAuthService {
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final MailService mailService;
	private final RedisService redisService;
	private final S3Uploader s3Uploader;

	private final UserModuleService userModuleService;

	/* 토큰 재발급 */
	@Transactional(readOnly = true)
	public ReissueResp reissue(String accessToken, String refreshToken) {
		// Refresh Token 유효성 검사
		jwtTokenProvider.isTokenValid(refreshToken);

		// Refresh Token 검증
		Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
		String redisRT = redisService.getObjectByKey(RedisService.REFRESH_TOKEN_PREFIX
			+ authentication.getName(), String.class);
		if (!redisRT.equals(refreshToken)) {
			throw new Exception400("Refresh Token", "정보가 일치하지 않습니다.");
		}

		// 유저 상태 검사
		User user = userModuleService.getUser(authentication.getName());

		// 기존 토큰 무효화
		jwtTokenProvider.invalidatedToken(accessToken);

		// 재발급
		String newAT = jwtTokenProvider.createAccessToken(user);
		String newRT = jwtTokenProvider.createRefreshToken();

		// 리프레시 토큰  Redis에 저장 ( key = "RT " + Email / value = refreshToken )
		redisService.setObjectByKey(RedisService.REFRESH_TOKEN_PREFIX + user.getEmail(), newRT,
			JwtTokenProvider.EXP_REFRESH, TimeUnit.MILLISECONDS);

		return new ReissueResp(newAT, newRT);
	}

	/* 회원 가입 */
	@Transactional(readOnly = true)
	public ExistsEmailResp checkExistsEmail(String email) {
		Boolean isExists = userModuleService.isEmailExists(email);
		return new ExistsEmailResp(email, isExists);
	}

	@Transactional
	public SignUpResp signUp(SignUpParam parameter) {
		userModuleService.validNewEmail(parameter.getEmail());
		userModuleService.validNickName(parameter.getNickName());

		String encodedPassword = passwordEncoder.encode(parameter.getPassword());
		String storedFileUrl = (parameter.getFile() == null) ? null : s3Uploader.saveFile(parameter.getFile());
		User user = User.createUser(parameter.getEmail(), encodedPassword, parameter.getNickName(),
			storedFileUrl, parameter.getAboutMe());

		user = userModuleService.save(user);

		return new SignUpResp(user.getEmail(), user.getNickName(), user.getProfileImageUrl());
	}

	/* 이메일 인증 */
	@Transactional
	public SendCodeForSignupResp sendCodeForSignUp(String email) {
		userModuleService.validNewEmail(email);

		// 이메일 전송
		String title = "[FoodieLog] 회원 가입 이메일 인증 번호";
		String authCode = this.createCode();
		mailService.sendEmail(email, title, authCode);

		// 이메일 인증 번호 Redis에 저장 ( key = "AuthCode " + Email / value = AuthCode )
		redisService.setObjectByKey(RedisService.EMAIL_AUTH_CODE_PREFIX + email, authCode, 3L, TimeUnit.MINUTES);

		return new SendCodeForSignupResp(email);
	}

	@Transactional
	public SendCodeForPasswordResp sendCodeForPassword(String email) {
		userModuleService.validEmail(email);

		// 이메일 전송
		String title = "[FoodieLog] 비밀번호 찾기 이메일 인증 번호";
		String authCode = this.createCode();
		mailService.sendEmail(email, title, authCode);

		// 이메일 인증 번호 Redis에 저장 ( key = "AuthCode " + Email / value = AuthCode )
		redisService.setObjectByKey(RedisService.EMAIL_AUTH_CODE_PREFIX + email, authCode, 3L, TimeUnit.MINUTES);

		return new SendCodeForPasswordResp(email);
	}

	@Transactional(readOnly = true)
	public VerifiedCodeResp verifiedCode(String email, String code) {
		String redisAuthCode = redisService.getObjectByKey(RedisService.EMAIL_AUTH_CODE_PREFIX + email, String.class);
		Boolean isVerified = redisAuthCode.equals(code);

		return new VerifiedCodeResp(email, code, isVerified);
	}

	private String createCode() {
		try {
			Random random = SecureRandom.getInstanceStrong();
			int randomNumber = random.nextInt(9000) + 1000;

			return Integer.toString(randomNumber);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception500("서버 에러: 이메일 인증 코드 생성 오류");
		}
	}

	/* 로그인 */
	@Transactional(readOnly = true)
	public LoginResp login(LoginParam parameter) {
		User user = userModuleService.getUser(parameter.getEmail());

		if (!passwordEncoder.matches(parameter.getPassword(), user.getPassword())) {
			throw new Exception400("password", ErrorMessage.PASSWORD_NOT_MATCH);
		}

		String accessToken = jwtTokenProvider.createAccessToken(user);
		String refreshToken = jwtTokenProvider.createRefreshToken();

		// 리프레시 토큰  Redis에 저장 ( key = "RT " + Email / value = refreshToken )
		redisService.setObjectByKey(RedisService.REFRESH_TOKEN_PREFIX + user.getEmail(), refreshToken,
			JwtTokenProvider.EXP_REFRESH, TimeUnit.MILLISECONDS);

		return new LoginResp(user, accessToken, refreshToken);
	}

	/* 프로필 설정 */
	@Transactional(readOnly = true)
	public ExistsNickNameResp checkExistsNickName(String nickName) {
		Boolean isExists = userModuleService.isNickNameExists(nickName);
		return new ExistsNickNameResp(nickName, isExists);
	}

	/* 비밀번호 변경 */
	@Transactional
	public ResetPasswordResp resetPassword(ResetPasswordParam parameter) {
		User user = userModuleService.getUser(parameter.getEmail());
		user.resetPassword(passwordEncoder.encode(parameter.getPassword()));
		return new ResetPasswordResp(parameter.getEmail());
	}
}
