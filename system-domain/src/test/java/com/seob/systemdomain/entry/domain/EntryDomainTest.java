package com.seob.systemdomain.entry.domain;

import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.user.domain.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EntryDomainTest {

    @Test
    @DisplayName("응모")
    void createEntry() {
        //given
        Long eventId = 1L;
        TicketDomain ticketDomain = TicketDomain.create(UserId.of("123"));

        //when
        EntryDomain entryDomain = EntryDomain.create(eventId, ticketDomain);

        //then
        assertThat(entryDomain).isNotNull();
        assertThat(entryDomain.getEventId()).isEqualTo(eventId);
        assertThat(entryDomain.getTicketDomain()).isEqualTo(ticketDomain);
        assertThat(entryDomain.getCreatedAt()).isNotNull();
    }
}