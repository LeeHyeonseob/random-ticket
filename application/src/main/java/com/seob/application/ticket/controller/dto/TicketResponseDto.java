package com.seob.application.ticket.controller.dto;

import com.seob.systemdomain.ticket.domain.TicketDomain;

import java.time.LocalDateTime;

public record TicketResponseDto(
        String id,
        String userId,
        boolean isUsed,
        LocalDateTime createdAt
) {

    public static TicketResponseDto of(TicketDomain ticket) {
        return new TicketResponseDto(
                ticket.getId().getValue(),
                ticket.getUserId().getValue(),
                ticket.isUsed(),
                ticket.getCreatedAt()
        );
    }


}
