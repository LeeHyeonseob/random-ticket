package com.seob.application.winner.controller.dto;

import com.seob.systemdomain.winner.dto.WinnerPublicInfo;

import java.time.LocalDateTime;

public record WinnerPublicResponse(
        Long eventId,
        String eventName,
        String maskedName,
        String maskedEmail,
        String eventDescription,
        LocalDateTime announcedAt
) {
    public static WinnerPublicResponse of(WinnerPublicInfo info) {
        return new WinnerPublicResponse(
                info.eventId(),
                info.eventName(),
                info.maskedName(),
                info.maskedEmail(),
                info.eventDescription(),
                info.announcedAt()
        );
    }
}
