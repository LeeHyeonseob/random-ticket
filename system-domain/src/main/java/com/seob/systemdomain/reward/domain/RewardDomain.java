package com.seob.systemdomain.reward.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class RewardDomain {
    private Long id;
    private Long eventId;
    private String resource_url;
    private LocalDateTime createdAt;

    public static RewardDomain create(Long eventId, String resource_url){
        RewardDomain rewardDomain = new RewardDomain();
        rewardDomain.eventId = eventId;
        rewardDomain.resource_url = resource_url;
        rewardDomain.createdAt = LocalDateTime.now();
        return rewardDomain;
    }

    public static RewardDomain of(Long id, Long eventId,String resource_url, LocalDateTime createdAt){
        RewardDomain rewardDomain = new RewardDomain();
        rewardDomain.id = id;
        rewardDomain.eventId = eventId;
        rewardDomain.resource_url = resource_url;
        rewardDomain.createdAt = createdAt;
        return rewardDomain;
    }

}
