package com.seob.systemdomain.entry.dto;

import java.time.LocalDateTime;


public record EntryInfo(
    Long id,
    Long eventId,
    String eventName,
    String ticketId,
    LocalDateTime createdAt
) {

    public static EntryInfo of(
        Long id,
        Long eventId,
        String eventName,
        String ticketId,
        LocalDateTime createdAt
    ) {
        return new EntryInfo(id, eventId, eventName, ticketId, createdAt);
    }
}
