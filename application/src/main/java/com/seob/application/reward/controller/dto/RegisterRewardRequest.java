package com.seob.application.reward.controller.dto;

public record RegisterRewardRequest(
        Long eventId,
        String rewardUrl
) {
    public static RegisterRewardRequest of(Long eventId, String rewardUrl) {
        return new RegisterRewardRequest(eventId, rewardUrl);
    }
}
