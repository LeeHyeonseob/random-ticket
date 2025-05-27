package com.seob.systemdomain.reward.service;

import com.seob.systemdomain.reward.domain.RewardDomain;

import java.util.List;

public interface RewardService {
    RewardDomain createReward(Long eventId, String rewardName, String resourceUrl);

    RewardDomain getRewardByEventId(Long eventId);

    List<RewardDomain> getAllRewards();

    RewardDomain updateReward(Long rewardId, String rewardName, String resourceUrl);

    void deleteReward(Long rewardId);
}
