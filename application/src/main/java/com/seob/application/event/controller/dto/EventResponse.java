package com.seob.application.event.controller.dto;

import com.seob.systemdomain.event.domain.EventDomain;


import java.time.LocalDate;
import java.time.LocalDateTime;

public record EventResponse(
         Long id,
         String name,
         String description,
         String status,
         LocalDate eventDate,
         LocalDateTime createdAt
) {

    public static EventResponse of(EventDomain eventDomain) {
        return new EventResponse(
                eventDomain.getId(),
                eventDomain.getName(),
                eventDomain.getDescription(),
                eventDomain.getStatus().name(),
                eventDomain.getEventDate(),
                eventDomain.getCreatedAt()
        );
    }
}
