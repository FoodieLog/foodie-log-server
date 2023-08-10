package com.foodielog.server._core.security.auth;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.foodielog.server.user.entity.User;
import com.foodielog.server.user.type.UserStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
 * 1. 시큐리티가 로그인 진행 한다.
 * 2. 로그인 유저 정보를 시큐리티 session에 저장한다(Security ContextHolder).
 * 3. 시큐리티 세션에 저장하는 타입 = Authentication
 * 4. Authentication 객체 안에 유저 정보를 저장 하는데, 저장 타입이 UserDetails
 *  */
@RequiredArgsConstructor
@Getter
public class PrincipalDetails implements UserDetails {

	private final User user;

	// 권한 확인
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(String.valueOf(user.getRole()));
		return Collections.singleton(grantedAuthority);
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	// 계정 만료 X ? -> T: 만료 전, F: 탈퇴(만료)
	@Override
	public boolean isAccountNonExpired() {
		return UserStatus.WITHDRAW != user.getStatus();
	}

	// 계정 잠금 X ? -> T: 잠금 전, F: 차단(잠금)
	@Override
	public boolean isAccountNonLocked() {
		return UserStatus.BLOCK != user.getStatus();
	}

	// 비밀번호 만료 X ? T: 만료 전, F: 만료
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	// 계정 활성화 ? T: 활성화, F: 비 활성화
	// @Todo 탈퇴, 차단 상태면 비 활성화 해야 하는지? isAccountNonExpired, isAccountNonLocked 과 차이점?
	@Override
	public boolean isEnabled() {
		return true;
	}
}
