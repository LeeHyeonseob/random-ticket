package com.seob.application.entry.controller.dto;

import com.seob.systemdomain.entry.dto.UserEventInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserEntryResponse(
    String eventName,
    LocalDate eventDate,
    LocalDateTime registeredAt
) {
    public static UserEntryResponse from(UserEventInfo info) {
        return new UserEntryResponse(
            info.eventName(),
            info.eventDate(),
            info.registeredAt()
        );
    }
}
