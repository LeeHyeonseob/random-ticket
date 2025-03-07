package com.seob.systemdomain.reward.service;

import com.seob.systemdomain.reward.domain.RewardDomain;
import com.seob.systemdomain.reward.dto.RewardPreview;

public interface RewardService {
    RewardDomain createReward(Long eventId, String resourceUrl);
    // 보상 미리보기 정보 조회 (사용자용)
    RewardDomain getRewardByEventId(Long eventId);
}
