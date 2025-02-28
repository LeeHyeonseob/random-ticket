package com.seob.systemdomain.entry.domain;


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
        String userId = "123";
        Long eventId = 1L;
        Long ticketId = 1L;

        //when
        EntryDomain entryDomain = EntryDomain.create(UserId.of(userId),eventId, ticketId);

        //then
        assertThat(entryDomain).isNotNull();
        assertThat(entryDomain.getUserId().getValue()).isEqualTo(userId);
        assertThat(entryDomain.getEventId()).isEqualTo(eventId);
        assertThat(entryDomain.getTicketId()).isEqualTo(ticketId);
        assertThat(entryDomain.getCreatedAt()).isNotNull();
    }
}