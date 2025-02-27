package com.seob.systemdomain.entry.domain;

import com.seob.systemdomain.ticket.domain.TicketDomain;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class EntryDomain {

    private Long id;
    private Long eventId;
    private TicketDomain ticketDomain;
    private LocalDateTime createdAt;

    public static EntryDomain create(Long eventId, TicketDomain ticketDomain) {
        EntryDomain entryDomain = new EntryDomain();
        entryDomain.eventId = eventId;
        entryDomain.ticketDomain = ticketDomain;
        entryDomain.createdAt = LocalDateTime.now();
        return entryDomain;
    }
}
