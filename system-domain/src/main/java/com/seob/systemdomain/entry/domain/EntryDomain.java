package com.seob.systemdomain.entry.domain;

import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.user.domain.vo.UserId;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class EntryDomain {

    private Long id;
    private UserId userId;
    private Long eventId;
    private Long ticketId;
    private LocalDateTime createdAt;

    public static EntryDomain create(UserId userId, Long eventId, Long ticketId) {
        EntryDomain entryDomain = new EntryDomain();
        entryDomain.userId = userId;
        entryDomain.eventId = eventId;
        entryDomain.ticketId = ticketId;
        entryDomain.createdAt = LocalDateTime.now();
        return entryDomain;
    }

    public static EntryDomain of(Long id, String userId, Long eventId, Long ticketId, LocalDateTime createdAt) {
        EntryDomain entryDomain = new EntryDomain();
        entryDomain.id = id;
        entryDomain.userId = UserId.of(userId);
        entryDomain.eventId = eventId;
        entryDomain.ticketId = ticketId;
        entryDomain.createdAt = createdAt;
        return entryDomain;
    }


}
