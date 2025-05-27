package com.seob.application.reward.service;

import com.seob.application.reward.controller.dto.RegisterRewardRequest;
import com.seob.application.reward.controller.dto.RewardResponse;

import java.util.List;

public interface RewardApplicationService {
    RewardResponse createReward(RegisterRewardRequest request);
    RewardResponse getRewardByEventId(Long eventId);
    List<RewardResponse> getAllRewards();
    RewardResponse updateReward(Long rewardId, RegisterRewardRequest request);
    void deleteReward(Long rewardId);
}
