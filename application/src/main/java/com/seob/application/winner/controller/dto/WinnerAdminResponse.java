package com.seob.application.winner.controller.dto;

import com.seob.systemdomain.winner.dto.WinnerDetailInfo;

import java.time.LocalDateTime;

public record WinnerAdminResponse(
        Long winnerId,
        String userId,
        String nickName,
        String userEmail,
        Long eventId,
        String eventName,
        Long rewardId,
        String status,
        LocalDateTime sentAt

) {
    public static WinnerAdminResponse of(WinnerDetailInfo info){
        return new WinnerAdminResponse(
                info.winnerId(),
                info.userId(),
                info.nickName(),
                info.userEmail(),
                info.eventId(),
                info.eventName(),
                info.rewardId(),
                info.status(),
                info.sentAt()
        );
    }
}
