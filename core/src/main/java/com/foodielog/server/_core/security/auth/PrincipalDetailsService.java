package com.foodielog.server._core.security.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.foodielog.server._core.error.exception.Exception404;
import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PrincipalDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	// login 호출
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		log.info("시큐리티 로그인 시도 email: " + email);

		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new Exception404("아이디(이메일)가 존재하지 않습니다."));

		return new PrincipalDetails(user);
	}
}
