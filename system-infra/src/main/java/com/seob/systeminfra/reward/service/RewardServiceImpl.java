package com.seob.systeminfra.reward.service;

import com.seob.systemdomain.reward.domain.RewardDomain;
import com.seob.systemdomain.reward.repository.RewardRepository;
import com.seob.systemdomain.reward.service.RewardService;
import com.seob.systeminfra.reward.exception.RewardNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    private final RewardRepository rewardRepository;

    @Override
    public RewardDomain createReward(Long eventId, String resourceUrl) {
        RewardDomain rewardDomain = RewardDomain.create(eventId, resourceUrl);
        RewardDomain saved = rewardRepository.save(rewardDomain);
        return saved;
    }

    @Override
    public RewardDomain getRewardByEventId(Long eventId) {
        return rewardRepository.findById(eventId).orElseThrow(() -> RewardNotFoundException.EXCEPTION);
    }
}
