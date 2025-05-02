package com.seob.application.winner.controller.dto;

import com.seob.systemdomain.winner.dto.WinnerUserDetailInfo;
import com.seob.systemdomain.winner.vo.RewardStatus;

import java.time.LocalDateTime;

public record WinnerUserResponse(
        Long winnerId,
        Long eventId,
        String eventName,
        String eventDescription,
        String rewardName,
        RewardStatus status,
        LocalDateTime sentAt
) {
    public static WinnerUserResponse of(WinnerUserDetailInfo info) {
        return new WinnerUserResponse(
                info.winnerId(),
                info.eventId(),
                info.eventName(),
                info.eventDescription(),
                info.rewardName(),
                info.status(),
                info.sentAt()
        );
    }
}
