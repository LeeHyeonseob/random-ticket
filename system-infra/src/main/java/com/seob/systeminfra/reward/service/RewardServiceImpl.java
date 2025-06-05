package com.seob.systeminfra.reward.service;

import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.reward.domain.RewardDomain;
import com.seob.systemdomain.reward.repository.RewardRepository;
import com.seob.systemdomain.reward.service.RewardService;
import com.seob.systeminfra.reward.exception.RewardDataAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    private final RewardRepository rewardRepository;
    private final EventRepository eventRepository;

    @Override
    public RewardDomain createReward(Long eventId, String rewardName, String resourceUrl) {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }

        if (!eventRepository.existsById(eventId)) {
            log.warn("보상 생성 실패: 존재하지 않는 이벤트 ID={}", eventId);
            throw RewardDataAccessException.EVENT_NOT_FOUND;
        }

        if (rewardRepository.existsByEventId(eventId)) {
            log.warn("보상 생성 실패: 이미 보상이 존재하는 이벤트 ID={}", eventId);
            throw RewardDataAccessException.REWARD_ALREADY_EXISTS;
        }

        RewardDomain rewardDomain = RewardDomain.create(eventId, rewardName, resourceUrl);
        return rewardRepository.save(rewardDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public RewardDomain getRewardByEventId(Long eventId) {
        RewardDomain reward = rewardRepository.findByEventId(eventId)
                .orElseThrow(() -> RewardDataAccessException.EVENT_NOT_FOUND);

        return reward;
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<RewardDomain> getAllRewards() {
        return rewardRepository.findAll();
    }

    @Override
    public RewardDomain updateReward(Long rewardId, String rewardName, String resourceUrl) {
        RewardDomain reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> RewardDataAccessException.NOT_FOUND);

        

        RewardDomain updatedReward = reward.update(rewardName, resourceUrl);
        return rewardRepository.save(updatedReward);
    }

    @Override
    public void deleteReward(Long rewardId) {
        RewardDomain rewardOpt = rewardRepository.findById(rewardId)
                .orElseThrow(() -> RewardDataAccessException.NOT_FOUND);
        
        rewardRepository.deleteById(rewardId);
    }
}
