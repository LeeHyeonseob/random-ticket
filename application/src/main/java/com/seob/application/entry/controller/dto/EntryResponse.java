package com.seob.application.entry.controller.dto;

import com.seob.systemdomain.entry.dto.EntryInfo;

import java.time.LocalDateTime;

public record EntryResponse(
    Long id,
    Long eventId,
    String eventName,
    String ticketId,
    LocalDateTime createdAt
) {
    public static EntryResponse of(EntryInfo info) {
        return new EntryResponse(
            info.id(),
            info.eventId(),
            info.eventName(),
            info.ticketId(),
            info.createdAt()
        );
    }
}
