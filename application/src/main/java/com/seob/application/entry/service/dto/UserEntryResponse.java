package com.seob.application.entry.service.dto;

import com.seob.systemdomain.entry.domain.EntryDomain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserEntryResponse(
        String eventName,
        LocalDate eventDate,
        LocalDateTime registeredAt
) {
    public static UserEntryResponse of(String eventName, LocalDate eventDate, LocalDateTime registeredAt) {
        return new UserEntryResponse(
                eventName,
                eventDate,
                registeredAt
        );
    }
}
