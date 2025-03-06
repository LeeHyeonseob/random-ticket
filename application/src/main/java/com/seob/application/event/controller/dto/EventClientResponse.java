package com.seob.application.event.controller.dto;

import com.seob.systemdomain.event.dto.EventDisplayInfo;

import java.time.LocalDate;

public record EventClientResponse(
        String name,
        String description,
        String status,
        LocalDate eventDate
) {
    public static EventClientResponse of(EventDisplayInfo eventDisplayInfo) {
        return new EventClientResponse(
                eventDisplayInfo.name(),
                eventDisplayInfo.description(),
                eventDisplayInfo.status(),
                eventDisplayInfo.eventDate()
        );
    }
}
