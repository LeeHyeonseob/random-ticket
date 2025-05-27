package com.seob.systemdomain.reward.domain;

import com.seob.systemcore.error.exception.RewardException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class RewardDomain {
    private Long id;
    private Long eventId;
    private String name;
    private String resourceUrl;
    private LocalDateTime createdAt;

    public static RewardDomain create(Long eventId, String name, String resourceUrl) {
        validateRewardFormat(name, resourceUrl);
        
        RewardDomain rewardDomain = new RewardDomain();
        rewardDomain.eventId = eventId;
        rewardDomain.name = name.trim();
        rewardDomain.resourceUrl = resourceUrl.trim();
        rewardDomain.createdAt = LocalDateTime.now();
        return rewardDomain;
    }

    public static RewardDomain of(Long id, Long eventId, String name, String resourceUrl, LocalDateTime createdAt) {
        RewardDomain rewardDomain = new RewardDomain();
        rewardDomain.id = id;
        rewardDomain.eventId = eventId;
        rewardDomain.name = name;
        rewardDomain.resourceUrl = resourceUrl;
        rewardDomain.createdAt = createdAt;
        return rewardDomain;
    }

    //보상 정보 업데이트
    public RewardDomain update(String newName, String newResourceUrl) {
        validateRewardFormat(newName, newResourceUrl);
        
        RewardDomain updatedReward = new RewardDomain();
        updatedReward.id = this.id;
        updatedReward.eventId = this.eventId;
        updatedReward.name = newName.trim();
        updatedReward.resourceUrl = newResourceUrl.trim();
        updatedReward.createdAt = this.createdAt;
        return updatedReward;
    }

    private static void validateRewardFormat(String name, String resourceUrl) {
        validateRewardName(name);
        validateResourceUrl(resourceUrl);
    }

    private static void validateRewardName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw RewardException.INVALID_REWARD_NAME;
        }
        if (name.length() > 100) {
            throw RewardException.INVALID_REWARD_NAME;
        }
    }

    private static void validateResourceUrl(String resourceUrl) {
        if (resourceUrl == null || resourceUrl.trim().isEmpty()) {
            throw RewardException.INVALID_REWARD_URL;
        }
        if (!resourceUrl.startsWith("http")) {
            throw RewardException.INVALID_REWARD_URL;
        }
    }
}
