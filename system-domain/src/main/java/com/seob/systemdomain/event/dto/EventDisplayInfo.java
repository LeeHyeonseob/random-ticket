package com.seob.systemdomain.event.dto;

import java.time.LocalDate;

public record EventDisplayInfo(
        String name,
        String description,
        String status,
        LocalDate eventDate
) {
    public static EventDisplayInfo of(String name, String description, String status, LocalDate eventDate) {
        return new EventDisplayInfo(name, description, status, eventDate);
    }
}
