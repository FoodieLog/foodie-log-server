package com.foodielog.application.withdrawUser.service;

import org.springframework.stereotype.Service;

import com.foodielog.server.admin.entity.WithdrawUser;
import com.foodielog.server.admin.repository.WithdrawUserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class WithdrawUserModuleService {
	private final WithdrawUserRepository withdrawUserRepository;

	public WithdrawUser save(WithdrawUser withdrawUser) {
		return withdrawUserRepository.save(withdrawUser);
	}
}
