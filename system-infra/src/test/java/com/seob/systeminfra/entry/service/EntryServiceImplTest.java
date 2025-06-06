package com.seob.systeminfra.entry.service;

import com.seob.systemdomain.entry.domain.EntryDomain;
import com.seob.systemdomain.entry.repository.EntryRepository;
import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.domain.vo.TicketId;
import com.seob.systemdomain.ticket.repository.TicketRepository;
import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systeminfra.exception.TicketNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntryServiceImplTest {

    @Mock
    private EntryRepository entryRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EntryServiceImpl entryService;

    @Test
    @DisplayName("티켓 ID 없이 이벤트 응모 성공")
    void apply_Success() {
        // given
        String userIdStr = "user123";
        Long eventId = 1L;
        UserId userId = UserId.of(userIdStr);
        String ticketIdStr = "ticket123";
        
        // 이벤트 모킹
        EventDomain eventDomain = mock(EventDomain.class);
        given(eventRepository.findById(eventId)).willReturn(Optional.of(eventDomain));
        
        // 티켓 모킹
        TicketDomain ticketDomain = mock(TicketDomain.class);
        TicketId ticketId = mock(TicketId.class);
        given(ticketId.getValue()).willReturn(ticketIdStr);
        given(ticketDomain.getId()).willReturn(ticketId);
        given(ticketRepository.findByUserIdAndEventIdAndNotUsed(eq(userId), eq(eventId)))
                .willReturn(Optional.of(ticketDomain));
        
        // 응모 결과 모킹
        EntryDomain createdEntry = mock(EntryDomain.class);
        given(entryRepository.save(any(EntryDomain.class))).willReturn(createdEntry);
        
        // when
        EntryDomain result = entryService.apply(userIdStr, eventId);
        
        // then
        assertThat(result).isEqualTo(createdEntry);
        verify(ticketRepository).save(ticketDomain);
        verify(entryRepository).save(any(EntryDomain.class));
    }
    
    @Test
    @DisplayName("이벤트를 찾을 수 없는 경우 예외 발생")
    void apply_EventNotFound() {
        // given
        String userIdStr = "user123";
        Long eventId = 1L;
        
        // 이벤트 없음
        given(eventRepository.findById(eventId)).willReturn(Optional.empty());
        
        // when & then
        assertThrows(com.seob.systeminfra.exception.EventNotFoundException.class, 
                () -> entryService.apply(userIdStr, eventId));
        
        // 티켓 조회 및 사용 처리가 일어나지 않음
        verify(ticketRepository, never()).findByUserIdAndEventIdAndNotUsed(any(), any());
        verify(entryRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("해당 이벤트에 사용 가능한 티켓이 없는 경우 예외 발생")
    void applyWithoutTicketId_NoTicketForEvent() {
        // given
        String userIdStr = "user123";
        Long eventId = 1L;
        UserId userId = UserId.of(userIdStr);
        
        // 이벤트 모킹
        EventDomain eventDomain = mock(EventDomain.class);
        given(eventRepository.findById(eventId)).willReturn(Optional.of(eventDomain));
        
        // 티켓 모킹 - 해당 이벤트용 티켓 없음
        given(ticketRepository.findByUserIdAndEventIdAndNotUsed(eq(userId), eq(eventId)))
                .willReturn(Optional.empty());
        
        // when & then
        assertThrows(TicketNotFoundException.class, () -> entryService.apply(userIdStr, eventId));
        
        // 티켓 사용 처리 및 엔트리 생성이 일어나지 않음
        verify(ticketRepository, never()).save(any());
        verify(entryRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("이벤트 ID가 null인 경우 NPE 발생")
    void apply() {
        // given
        String userIdStr = "user123";
        Long eventId = null;
        
        // NPE가 발생하더라도 findById는 호출됨을 알림
        when(eventRepository.findById(null)).thenThrow(NullPointerException.class);
        
        // when & then
        assertThrows(NullPointerException.class, () -> entryService.apply(userIdStr, eventId));
        
        // 티켓 조회 및 사용 처리가 일어나지 않음
        verify(ticketRepository, never()).findByUserIdAndEventIdAndNotUsed(any(), any());
        verify(entryRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("이벤트 응모 전체 과정 통합 테스트")
    void apply_IntegrationFlow() {
        // given
        String userIdStr = "user123";
        Long eventId = 1L;
        UserId userId = UserId.of(userIdStr);
        String ticketIdStr = "ticket123";
        
        // 실제 객체에 가까운 모킹
        EventDomain eventDomain = mock(EventDomain.class);
        given(eventRepository.findById(eventId)).willReturn(Optional.of(eventDomain));
        
        // 티켓 도메인 상세 모킹
        TicketId ticketId = mock(TicketId.class);
        given(ticketId.getValue()).willReturn(ticketIdStr);
        
        TicketDomain ticketDomain = mock(TicketDomain.class);
        given(ticketDomain.getId()).willReturn(ticketId);
        
        given(ticketRepository.findByUserIdAndEventIdAndNotUsed(any(UserId.class), eq(eventId)))
                .willReturn(Optional.of(ticketDomain));
        
        // EntryDomain 생성 검증을 위한 모킹
        EntryDomain expectedEntry = mock(EntryDomain.class);
        given(entryRepository.save(any(EntryDomain.class))).willReturn(expectedEntry);
        
        // when
        EntryDomain result = entryService.apply(userIdStr, eventId);
        
        // then
        assertThat(result).isEqualTo(expectedEntry);
        
        // 핵심 메소드 호출 검증
        verify(eventRepository).findById(eventId);
        verify(ticketRepository).findByUserIdAndEventIdAndNotUsed(any(UserId.class), eq(eventId));
        verify(ticketRepository).save(ticketDomain);
        verify(entryRepository).save(any(EntryDomain.class));
    }
}
