package com.seob.systemdomain.entry.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserEventInfo(
        String eventName,
        LocalDate eventDate,
        LocalDateTime registeredAt
) {
    public static UserEventInfo of(String eventName, LocalDate eventDate, LocalDateTime registeredAt) {
        return new UserEventInfo(eventName, eventDate, registeredAt);
    }
}
