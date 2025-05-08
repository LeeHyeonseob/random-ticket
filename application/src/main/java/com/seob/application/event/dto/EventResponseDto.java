package com.seob.application.event.dto;

import com.seob.systemdomain.event.domain.EventDomain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EventResponseDto(
    Long id,
    String name,
    String description,
    String status,
    LocalDate eventDate,
    LocalDateTime createdAt
) {
    public static EventResponseDto from(EventDomain event) {
        return new EventResponseDto(
            event.getId(),
            event.getName(),
            event.getDescription(),
            event.getStatus().name(),
            event.getEventDate(),
            event.getCreatedAt()
        );
    }
}