package com.seob.systemdomain.reward.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class RewardDomain {
    private Long id;
    private Long eventId;
    private String name;
    private String resourceUrl;
    private LocalDateTime createdAt;

    public static RewardDomain create(Long eventId, String name, String resourceUrl){
        RewardDomain rewardDomain = new RewardDomain();
        rewardDomain.eventId = eventId;
        rewardDomain.name = name;
        rewardDomain.resourceUrl = resourceUrl;
        rewardDomain.createdAt = LocalDateTime.now();
        return rewardDomain;
    }

    public static RewardDomain of(Long id, Long eventId, String name, String resourceUrl, LocalDateTime createdAt){
        RewardDomain rewardDomain = new RewardDomain();
        rewardDomain.id = id;
        rewardDomain.eventId = eventId;
        rewardDomain.name = name;
        rewardDomain.resourceUrl = resourceUrl;
        rewardDomain.createdAt = createdAt;
        return rewardDomain;
    }

}
