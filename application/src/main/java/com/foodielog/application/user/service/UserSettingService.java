package com.foodielog.application.user.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.foodielog.application.user.dto.request.ChangeNotificationParam;
import com.foodielog.application.user.dto.request.ChangePasswordParam;
import com.foodielog.application.user.dto.request.ChangeProfileParam;
import com.foodielog.application.user.dto.request.WithdrawParam;
import com.foodielog.application.user.dto.response.ChangeNotificationResp;
import com.foodielog.application.user.dto.response.ChangePasswordResp;
import com.foodielog.application.user.dto.response.ChangeProfileResp;
import com.foodielog.application.user.dto.response.CheckBadgeApplyResp;
import com.foodielog.application.user.dto.response.CreateBadgeApplyResp;
import com.foodielog.application.user.dto.response.LogoutResp;
import com.foodielog.application.user.dto.response.WithdrawResp;
import com.foodielog.server._core.error.ErrorMessage;
import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server._core.redis.RedisService;
import com.foodielog.server._core.s3.S3Uploader;
import com.foodielog.server._core.security.jwt.JwtTokenProvider;
import com.foodielog.server.admin.entity.BadgeApply;
import com.foodielog.server.admin.entity.WithdrawUser;
import com.foodielog.server.admin.repository.BadgeApplyRepository;
import com.foodielog.server.admin.repository.WithdrawUserRepository;
import com.foodielog.server.feed.entity.Feed;
import com.foodielog.server.feed.repository.FeedRepository;
import com.foodielog.server.feed.type.ContentStatus;
import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.reply.repository.ReplyRepository;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserSettingService {
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisService redisService;
	private final S3Uploader s3Uploader;

	private final UserRepository userRepository;
	private final FeedRepository feedRepository;
	private final ReplyRepository replyRepository;
	private final BadgeApplyRepository badgeApplyRepository;
	private final WithdrawUserRepository withdrawUserRepository;

	@Transactional
	public ChangeNotificationResp changeNotification(ChangeNotificationParam parameter) {
		User user = parameter.getUser();
		user.changeNotificationFlag(parameter.getFlag());
		userRepository.save(user);

		return new ChangeNotificationResp(user, parameter.getFlag());
	}

	@Transactional(readOnly = true)
	public CheckBadgeApplyResp checkBadgeApply(User user) {
		Timestamp createdAt = badgeApplyRepository.findByUserId(user.getId())
			.map(BadgeApply::getCreatedAt)
			.orElseGet(() -> null);

		return new CheckBadgeApplyResp(user, createdAt);
	}

	@Transactional
	public CreateBadgeApplyResp creatBadgeApply(User user) {
		BadgeApply badgeApply = BadgeApply.createBadgeApply(user);
		badgeApplyRepository.save(badgeApply);

		return new CreateBadgeApplyResp(user, badgeApply);
	}

	@Transactional
	public ChangePasswordResp changePassword(ChangePasswordParam parameter) {
		User user = parameter.getUser();

		if (!passwordEncoder.matches(parameter.getOldPassword(), user.getPassword())) {
			throw new Exception400("password", ErrorMessage.PASSWORD_NOT_MATCH);
		}

		user.resetPassword(passwordEncoder.encode(parameter.getNewPassword()));

		userRepository.save(user);

		return new ChangePasswordResp(user.getEmail());
	}

	@Transactional
	public LogoutResp logout(String accessToken) {
		String email = jwtTokenProvider.invalidatedToken(accessToken);

		return new LogoutResp(email, Boolean.TRUE);
	}

	@Transactional
	public WithdrawResp withdraw(WithdrawParam parameter) {
		User user = parameter.getUser();
		// 탈퇴 유저 저장
		Long feedCount = feedRepository.countByUserAndStatus(user, ContentStatus.NORMAL);
		Long replyCount = replyRepository.countByUserAndStatus(user, ContentStatus.NORMAL);

		WithdrawUser withdrawUser = WithdrawUser.createWithdrawUser(user, feedCount, replyCount,
			parameter.getWithdrawReason());
		withdrawUserRepository.save(withdrawUser);

		// 유저, 피드, 댓글 상태 변경
		user.withdraw();
		userRepository.save(user);

		List<Feed> feedList = feedRepository.findByUserIdAndStatus(user.getId(), ContentStatus.NORMAL);
		feedList.forEach(Feed::deleteFeed);

		List<Reply> replyList = replyRepository.findByUserIdAndStatus(user.getId(), ContentStatus.NORMAL);
		replyList.forEach(Reply::deleteReply);

		// 토큰 무효화
		jwtTokenProvider.invalidatedToken(parameter.getAccessToken());

		return new WithdrawResp(user.getEmail(), Boolean.TRUE);
	}

	@Transactional
	public ChangeProfileResp ChangeProfile(ChangeProfileParam parameter) {
		User user = parameter.getUser();

		// 기존 닉네임과 일치 하지 않은데 중복이라면 중복! (기존 닉네임과 동일 하다면 닉네임을 변경 하는게 아님)
		if (!user.getNickName().equals(parameter.getNickName())
			&& userRepository.existsByNickName(parameter.getNickName())
		) {
			throw new Exception400("nickName", "이미 사용 중인 닉네임 입니다");
		}

		String storedFileUrl = getStoredFileUrl(user.getProfileImageUrl(), parameter.getFile());
		user.changeProfile(parameter.getNickName(), storedFileUrl, parameter.getAboutMe());

		userRepository.save(user);

		return new ChangeProfileResp(user.getNickName(), user.getProfileImageUrl(), user.getAboutMe());
	}

	private String getStoredFileUrl(String userProfileImageUrl, MultipartFile file) {
		String storedFileUrl = userProfileImageUrl;

		if ((file != null)) {
			storedFileUrl = s3Uploader.saveFile(file);

			// 기존 이미지 삭제
			if (userProfileImageUrl != null) {
				s3Uploader.deleteFile(userProfileImageUrl);
			}
		}

		return storedFileUrl;
	}
}
