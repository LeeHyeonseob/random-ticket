package com.seob.systemdomain.reward.service;

import com.seob.systemdomain.entry.domain.EntryDomain;
import com.seob.systemdomain.reward.domain.RewardDomain;
import com.seob.systemdomain.reward.dto.RewardPreview;

import java.util.Optional;

public interface RewardService {
    RewardDomain createReward(Long eventId, String resourceUrl);
    // 보상 미리보기 정보 조회 (사용자용)
    RewardPreview getRewardPreviewByEventId(Long eventId);
}
