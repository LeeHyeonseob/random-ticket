package com.seob.application.ticket.controller.dto;

import com.seob.systemdomain.ticket.domain.TicketDomain;

import java.time.LocalDateTime;

public record TicketResponseDto(
        String id,
        String userId,
        Long eventId,
        boolean isUsed,
        LocalDateTime createdAt,
        LocalDateTime expiryDate
) {

    public static TicketResponseDto of(TicketDomain ticket) {
        return new TicketResponseDto(
                ticket.getId().getValue(),
                ticket.getUserId().getValue(),
                ticket.getEventId(),
                ticket.isUsed(),
                ticket.getCreatedAt(),
                ticket.getExpiryDate()
        );
    }
}
