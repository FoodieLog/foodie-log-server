package com.foodielog.application.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.foodielog.server._core.error.ErrorMessage;
import com.foodielog.server._core.error.exception.Exception400;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.repository.UserRepository;
import com.foodielog.server.user.type.UserStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserModuleService {
	private final UserRepository userRepository;

	public User get(String email) {
		return userRepository.findByEmailAndStatus(email, UserStatus.NORMAL)
			.orElseThrow(() -> new Exception400("email", ErrorMessage.USER_NOT_FOUND));
	}

	public User get(Long id) {
		return userRepository.findByIdAndStatus(id, UserStatus.NORMAL)
			.orElseThrow(() -> new Exception400("id", ErrorMessage.USER_NOT_FOUND));
	}

	public Boolean isEmailExists(String email) {
		return userRepository.existsByEmail(email);
	}

	public Boolean isNickNameExists(String nickName) {
		return userRepository.existsByNickName(nickName);
	}

	public void checkEmailExists(String email) {
		if (!isEmailExists(email)) {
			throw new Exception400("email", "해당 이메일 정보가 없습니다.");
		}
	}

	public void checkNewEmail(String email) {
		if (isEmailExists(email)) {
			throw new Exception400("email", "이미 가입된 이메일 입니다");
		}
	}

	public void checkNewNickName(String nickName) {
		if (isNickNameExists(nickName)) {
			throw new Exception400("nickName", "이미 사용 중인 닉네임 입니다");
		}
	}

	public User save(User user) {
		return userRepository.save(user);
	}

	public List<User> searchUsers(String keyword) {
		return userRepository.searchUserOrderByFollowedIdDesc(keyword);
	}
}
