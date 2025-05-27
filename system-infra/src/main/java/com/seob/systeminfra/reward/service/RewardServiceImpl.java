package com.seob.systeminfra.reward.service;

import com.seob.systemcore.error.exception.RewardException;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.reward.domain.RewardDomain;
import com.seob.systemdomain.reward.repository.RewardRepository;
import com.seob.systemdomain.reward.service.RewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw RewardException.INVALID_EVENT_ID;
        }

        validateEventExists(eventId);
        validateRewardNotExists(eventId);

        RewardDomain rewardDomain = RewardDomain.create(eventId, rewardName, resourceUrl);
        return rewardRepository.save(rewardDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public RewardDomain getRewardByEventId(Long eventId) {
        return rewardRepository.findByEventId(eventId)
                .orElseThrow(() -> {
                    log.warn("보상 조회 실패: 이벤트 ID={}에 보상이 없습니다", eventId);
                    return RewardException.EVENT_NOT_FOUND_FOR_REWARD;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<RewardDomain> getAllRewards() {
        return rewardRepository.findAll();
    }

    @Override
    public RewardDomain updateReward(Long rewardId, String rewardName, String resourceUrl) {
        RewardDomain existingReward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> {
                    log.warn("보상 수정 실패: 보상 ID={}를 찾을 수 없습니다", rewardId);
                    return RewardException.EVENT_NOT_FOUND_FOR_REWARD;
                });
        
        RewardDomain updatedReward = existingReward.update(rewardName, resourceUrl);
        return rewardRepository.save(updatedReward);
    }

    @Override
    public void deleteReward(Long rewardId) {
        if (rewardRepository.findById(rewardId).isEmpty()) {
            log.warn("보상 삭제 실패: 보상 ID={}를 찾을 수 없습니다", rewardId);
            throw RewardException.EVENT_NOT_FOUND_FOR_REWARD;
        }
        
        rewardRepository.deleteById(rewardId);
    }

    private void validateEventExists(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            log.warn("보상 생성 실패: 존재하지 않는 이벤트 ID={}", eventId);
            throw RewardException.EVENT_NOT_FOUND_FOR_REWARD;
        }
    }

    private void validateRewardNotExists(Long eventId) {
        if (rewardRepository.existsByEventId(eventId)) {
            log.warn("보상 생성 실패: 이미 보상이 존재하는 이벤트 ID={}", eventId);
            throw RewardException.REWARD_ALREADY_EXISTS;
        }
    }
}
