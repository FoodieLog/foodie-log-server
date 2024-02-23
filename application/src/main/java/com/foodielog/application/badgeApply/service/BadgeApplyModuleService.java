package com.foodielog.application.badgeApply.service;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.foodielog.server.admin.entity.BadgeApply;
import com.foodielog.server.admin.repository.BadgeApplyRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BadgeApplyModuleService {
	private final BadgeApplyRepository badgeApplyRepository;

	public BadgeApply save(BadgeApply badgeApply) {
		return badgeApplyRepository.save(badgeApply);
	}

	public Timestamp getCreatedAt(Long userId) {
		Optional<BadgeApply> optional = badgeApplyRepository.findByUserId(userId);

		if (optional.isPresent()) {
			return optional.get().getCreatedAt();
		}

		return null;
	}
}
