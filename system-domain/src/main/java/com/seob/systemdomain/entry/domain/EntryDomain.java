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
    private Long ticketId;
    private LocalDateTime createdAt;

    public static EntryDomain create(Long eventId, Long ticketId) {
        EntryDomain entryDomain = new EntryDomain();
        entryDomain.eventId = eventId;
        entryDomain.ticketId = ticketId;
        entryDomain.createdAt = LocalDateTime.now();
        return entryDomain;
    }
}
