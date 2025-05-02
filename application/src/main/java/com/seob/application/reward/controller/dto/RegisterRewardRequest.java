package com.seob.application.reward.controller.dto;

public record RegisterRewardRequest(
        Long eventId,
        String rewardName,
        String rewardUrl
) {
    public static RegisterRewardRequest of(Long eventId, String rewardName, String rewardUrl) {
        return new RegisterRewardRequest(eventId, rewardName, rewardUrl);
    }
}
