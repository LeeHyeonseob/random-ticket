package com.seob.systemdomain.entry.domain;

import com.seob.systemdomain.user.domain.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EntryDomainTest {

    @Test
    @DisplayName("Entry 생성 성공")
    void create_Success() {
        // given
        String userIdStr = "user123";
        UserId userId = new UserId(userIdStr);
        Long eventId = 1L;
        String ticketId = "ticket123";
        
        // when
        EntryDomain entry = EntryDomain.create(userId, eventId, ticketId);
        
        // then
        assertThat(entry).isNotNull();
        assertThat(entry.getUserId()).isEqualTo(userId);
        assertThat(entry.getUserId().getValue()).isEqualTo(userIdStr);
        assertThat(entry.getEventId()).isEqualTo(eventId);
        assertThat(entry.getTicketId()).isEqualTo(ticketId);
        assertThat(entry.getCreatedAt()).isNotNull();
        // 생성 시간은 현재 시간과 유사해야 함 (1초 이내 차이)
        assertThat(entry.getCreatedAt()).isBetween(
                LocalDateTime.now().minusSeconds(1),
                LocalDateTime.now().plusSeconds(1)
        );
    }
    
    @Test
    @DisplayName("기존 Entry 데이터로부터 객체 생성")
    void of_Success() {
        // given
        Long id = 1L;
        String userIdStr = "user123";
        Long eventId = 1L;
        String ticketId = "ticket123";
        LocalDateTime createdAt = LocalDateTime.now().minusHours(1);
        
        // when
        EntryDomain entry = EntryDomain.of(id, userIdStr, eventId, ticketId, createdAt);
        
        // then
        assertThat(entry).isNotNull();
        assertThat(entry.getId()).isEqualTo(id);
        assertThat(entry.getUserId().getValue()).isEqualTo(userIdStr);
        assertThat(entry.getEventId()).isEqualTo(eventId);
        assertThat(entry.getTicketId()).isEqualTo(ticketId);
        assertThat(entry.getCreatedAt()).isEqualTo(createdAt);
    }
    
    @Test
    @DisplayName("다른 사용자와 이벤트로 Entry 생성")
    void create_DifferentUserAndEvent() {
        // given
        String userIdStr1 = "user123";
        String userIdStr2 = "user456";
        UserId userId1 = new UserId(userIdStr1);
        UserId userId2 = new UserId(userIdStr2);
        Long eventId1 = 1L;
        Long eventId2 = 2L;
        String ticketId1 = "ticket123";
        String ticketId2 = "ticket456";
        
        // when
        EntryDomain entry1 = EntryDomain.create(userId1, eventId1, ticketId1);
        EntryDomain entry2 = EntryDomain.create(userId2, eventId2, ticketId2);
        
        // then
        assertThat(entry1).isNotNull();
        assertThat(entry2).isNotNull();
        assertThat(entry1.getUserId()).isNotEqualTo(entry2.getUserId());
        assertThat(entry1.getEventId()).isNotEqualTo(entry2.getEventId());
        assertThat(entry1.getTicketId()).isNotEqualTo(entry2.getTicketId());
    }
    
    @Test
    @DisplayName("같은 사용자, 다른 이벤트로 Entry 생성")
    void create_SameUserDifferentEvent() {
        // given
        String userIdStr = "user123";
        UserId userId = new UserId(userIdStr);
        Long eventId1 = 1L;
        Long eventId2 = 2L;
        String ticketId1 = "ticket123";
        String ticketId2 = "ticket456";
        
        // when
        EntryDomain entry1 = EntryDomain.create(userId, eventId1, ticketId1);
        EntryDomain entry2 = EntryDomain.create(userId, eventId2, ticketId2);
        
        // then
        assertThat(entry1).isNotNull();
        assertThat(entry2).isNotNull();
        assertThat(entry1.getUserId()).isEqualTo(entry2.getUserId());
        assertThat(entry1.getEventId()).isNotEqualTo(entry2.getEventId());
        assertThat(entry1.getTicketId()).isNotEqualTo(entry2.getTicketId());
    }
}
