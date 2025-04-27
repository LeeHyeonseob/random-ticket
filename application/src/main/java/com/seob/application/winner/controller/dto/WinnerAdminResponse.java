package com.seob.application.winner.controller.dto;

import com.seob.systemdomain.winner.dto.WinnerRewardDetailInfo;

import java.time.LocalDateTime;

public record WinnerAdminResponse(
        Long winnerId,
        String userId,
        String nickName,
        String userEmail,
        Long eventId,
        String eventName,
        String eventDescription,
        Long rewardId,
        String status,
        LocalDateTime sentAt

) {
    public static WinnerAdminResponse of(WinnerRewardDetailInfo info){
        return new WinnerAdminResponse(
                info.winnerId(),
                info.userId(),
                info.nickName(),
                info.userEmail(),
                info.eventId(),
                info.eventName(),
                info.eventDescription(),
                info.rewardId(),
                info.status(),
                info.sentAt()
        );
    }
}
