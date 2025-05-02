package com.seob.systemdomain.winner.dto;

import com.seob.systemdomain.winner.vo.RewardStatus;

import java.time.LocalDateTime;

public record WinnerUserDetailInfo(
        Long winnerId,
        Long eventId,
        String eventName,
        String eventDescription,
        String rewardName,
        RewardStatus status,
        LocalDateTime sentAt
) {
}
