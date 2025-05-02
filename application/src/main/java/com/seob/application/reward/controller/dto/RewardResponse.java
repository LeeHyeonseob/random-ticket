package com.seob.application.reward.controller.dto;

import com.seob.systemdomain.reward.domain.RewardDomain;

import java.time.LocalDateTime;

public record RewardResponse(
        Long id,
        Long eventId,
        String rewardName,
        String rewardUrl,
        LocalDateTime createdAt) {
    public static RewardResponse of(RewardDomain rewardDomain) {
        return new RewardResponse(
                rewardDomain.getId(),
                rewardDomain.getEventId(),
                rewardDomain.getName(),
                rewardDomain.getResourceUrl(),
                rewardDomain.getCreatedAt()
        );
    }
}
