package com.seob.systemdomain.winner.dto;

import java.time.LocalDateTime;

public record WinnerUserDetailInfo(
        Long winnerId,
        Long eventId,
        String eventName,
        String eventDescription,
        String status,
        LocalDateTime sentAt
) {
}
