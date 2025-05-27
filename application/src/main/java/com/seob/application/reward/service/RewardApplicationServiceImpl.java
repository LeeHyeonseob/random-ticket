package com.seob.application.reward.service;

import com.seob.application.reward.controller.dto.RegisterRewardRequest;
import com.seob.application.reward.controller.dto.RewardResponse;
import com.seob.systemdomain.reward.domain.RewardDomain;
import com.seob.systemdomain.reward.service.RewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RewardApplicationServiceImpl implements RewardApplicationService {

    private final RewardService rewardService;

    @Override
    public RewardResponse createReward(RegisterRewardRequest request) {
        log.info("보상 생성 요청: eventId={}, rewardName={}", request.eventId(), request.rewardName());
        
        RewardDomain rewardDomain = rewardService.createReward(
                request.eventId(),
                request.rewardName(),
                request.rewardUrl()
        );
        
        log.info("보상 생성 완료: rewardId={}", rewardDomain.getId());
        return RewardResponse.of(rewardDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public RewardResponse getRewardByEventId(Long eventId) {
        log.debug("이벤트 보상 조회: eventId={}", eventId);
        
        RewardDomain rewardDomain = rewardService.getRewardByEventId(eventId);
        return RewardResponse.of(rewardDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RewardResponse> getAllRewards() {
        log.debug("전체 보상 목록 조회");
        
        List<RewardDomain> rewards = rewardService.getAllRewards();
        return rewards.stream()
                .map(RewardResponse::of)
                .collect(Collectors.toList());
    }

    @Override
    public RewardResponse updateReward(Long rewardId, RegisterRewardRequest request) {
        log.info("보상 수정 요청: rewardId={}, rewardName={}", rewardId, request.rewardName());
        
        RewardDomain updatedReward = rewardService.updateReward(
                rewardId,
                request.rewardName(),
                request.rewardUrl()
        );
        
        log.info("보상 수정 완료: rewardId={}", rewardId);
        return RewardResponse.of(updatedReward);
    }

    @Override
    public void deleteReward(Long rewardId) {
        log.info("보상 삭제 요청: rewardId={}", rewardId);
        
        rewardService.deleteReward(rewardId);
        
        log.info("보상 삭제 완료: rewardId={}", rewardId);
    }
}
