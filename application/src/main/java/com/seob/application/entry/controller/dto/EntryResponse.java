package com.seob.application.entry.controller.dto;

import com.seob.systemdomain.entry.domain.EntryDomain;

import java.time.LocalDateTime;

public record EntryResponse(
        Long id,
        String userId,
        Long eventId,
        String ticketId,
        LocalDateTime createdAt
) {
    public static EntryResponse of(EntryDomain entryDomain) {
        return new EntryResponse(
                entryDomain.getId(),
                entryDomain.getUserId().getValue(),
                entryDomain.getEventId(),
                entryDomain.getTicketId(),
                entryDomain.getCreatedAt());
    }


}
