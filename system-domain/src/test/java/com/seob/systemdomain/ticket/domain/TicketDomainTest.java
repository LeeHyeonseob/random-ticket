package com.seob.systemdomain.ticket.domain;

import com.seob.systemdomain.ticket.domain.vo.TicketId;
import com.seob.systemdomain.user.domain.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TicketDomainTest {

    @Test
    @DisplayName("티켓 생성 성공")
    void create_Success() {
        // given
        String ticketIdStr = "ticket123";
        String userIdStr = "user123";
        Long eventId = 1L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = now.plusDays(30);
        
        // when
        TicketDomain ticket = TicketDomain.of(
                ticketIdStr,
                new UserId(userIdStr),
                eventId,
                now,
                null,
                expiryDate,
                false,
                false
        );
        
        // then
        assertThat(ticket).isNotNull();
        assertThat(ticket.getId().getValue()).isEqualTo(ticketIdStr);
        assertThat(ticket.getUserId().getValue()).isEqualTo(userIdStr);
        assertThat(ticket.getEventId()).isEqualTo(eventId);
        assertThat(ticket.isUsed()).isFalse();
        assertThat(ticket.getUsedAt()).isNull();
        assertThat(ticket.getExpiryDate()).isEqualTo(expiryDate);
        assertThat(ticket.getIsExpired()).isFalse();
    }
    
    @Test
    @DisplayName("티켓 사용 처리 성공")
    void use_Success() {
        // given
        String ticketIdStr = "ticket123";
        String userIdStr = "user123";
        Long eventId = 1L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = now.plusDays(30);
        
        TicketDomain ticket = TicketDomain.of(
                ticketIdStr,
                new UserId(userIdStr),
                eventId,
                now,
                null,
                expiryDate,
                false,
                false
        );
        
        // when
        ticket.use();
        
        // then
        assertThat(ticket.isUsed()).isTrue();
        assertThat(ticket.getUsedAt()).isNotNull();
        // 사용 시간은 현재 시간과 유사해야 함 (1초 이내 차이)
        assertThat(ticket.getUsedAt()).isBetween(
                LocalDateTime.now().minusSeconds(1),
                LocalDateTime.now().plusSeconds(1)
        );
    }
    
    @Test
    @DisplayName("이미 사용된 티켓 확인")
    void isUsed_AlreadyUsed() {
        // given
        String ticketIdStr = "ticket123";
        String userIdStr = "user123";
        Long eventId = 1L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime usedAt = now.minusDays(1);
        LocalDateTime expiryDate = now.plusDays(30);
        
        // when - 이미 사용된 상태로 생성
        TicketDomain ticket = TicketDomain.of(
                ticketIdStr,
                new UserId(userIdStr),
                eventId,
                now.minusDays(2),
                usedAt,
                expiryDate,
                true,
                false
        );
        
        // then
        assertThat(ticket.isUsed()).isTrue();
        assertThat(ticket.getUsedAt()).isEqualTo(usedAt);
    }
    
    @Test
    @DisplayName("만료된 티켓 확인")
    void isExpired_AlreadyExpired() {
        // given
        String ticketIdStr = "ticket123";
        String userIdStr = "user123";
        Long eventId = 1L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = now.minusDays(1); // 이미 만료됨
        
        // when - 만료된 상태로 생성
        TicketDomain ticket = TicketDomain.of(
                ticketIdStr,
                new UserId(userIdStr),
                eventId,
                now.minusDays(30),
                null,
                expiryDate,
                false,
                true
        );
        
        // then
        assertThat(ticket.getIsExpired()).isTrue();
        assertThat(ticket.getExpiryDate()).isEqualTo(expiryDate);
    }
    
    @Test
    @DisplayName("티켓 사용 여부 변경")
    void setUsed_ChangeStatus() {
        // given
        String ticketIdStr = "ticket123";
        String userIdStr = "user123";
        Long eventId = 1L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = now.plusDays(30);
        
        TicketDomain ticket = TicketDomain.of(
                ticketIdStr,
                new UserId(userIdStr),
                eventId,
                now,
                null,
                expiryDate,
                false,
                false
        );
        
        // when - 사용 처리
        ticket.use();
        
        // then
        assertThat(ticket.isUsed()).isTrue();
        assertThat(ticket.getUsedAt()).isNotNull();
        
        // 추가: 동일한 id로 새로운 티켓을 쿼리했을 때
        TicketId ticketId = new TicketId(ticketIdStr);
        assertThat(ticketId.getValue()).isEqualTo(ticketIdStr);
    }
}
