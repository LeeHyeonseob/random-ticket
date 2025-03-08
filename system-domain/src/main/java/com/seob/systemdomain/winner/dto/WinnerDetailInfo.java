package com.seob.systemdomain.winner.dto;

import java.time.LocalDateTime;

public record WinnerDetailInfo(
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
}
