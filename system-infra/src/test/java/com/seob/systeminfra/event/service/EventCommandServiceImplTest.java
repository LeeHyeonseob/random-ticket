package com.seob.systeminfra.event.service;

import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systeminfra.event.exception.EventNotFoundException;
import com.seob.systeminfra.event.exception.InvalidEventStatusException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventCommandServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventCommandServiceImpl eventCommandService;

    @Test
    @DisplayName("Event 생성 성공")
    void createEvent_Success() {
        // given
        String name = "테스트 Event";
        String description = "Event 설명";
        LocalDate eventDate = LocalDate.now().plusDays(1); // 미래 날짜
        
        EventDomain mockEventDomain = mock(EventDomain.class);
        
        // EventDomain.create 메서드가 static이라 stubbing 불가 -> 실제 호출됨
        given(eventRepository.save(any(EventDomain.class))).willReturn(mockEventDomain);
        
        // when
        EventDomain result = eventCommandService.createEvent(name, description, eventDate);
        
        // then
        assertThat(result).isEqualTo(mockEventDomain);
        verify(eventRepository).save(any(EventDomain.class));
    }
    
    @Test
    @DisplayName("Event 생성 시 과거 날짜면 실패")
    void createEvent_PastDate_ThrowsException() {
        // given
        String name = "테스트 Event";
        String description = "Event 설명";
        LocalDate pastDate = LocalDate.now().minusDays(1);
        
        // when & then
        assertThrows(IllegalArgumentException.class, 
                   () -> eventCommandService.createEvent(name, description, pastDate));
        
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Event 상태를 OPEN으로 변경 성공")
    void changeStatus_ToOpen_Success() {
        // given
        Long eventId = 1L;
        String eventStatus = "OPEN";
        
        EventDomain mockEventDomain = mock(EventDomain.class);
        given(eventRepository.findById(eventId)).willReturn(mockEventDomain);
        given(eventRepository.save(mockEventDomain)).willReturn(mockEventDomain);
        
        // when
        EventDomain result = eventCommandService.changeStatus(eventId, eventStatus);
        
        // then
        assertThat(result).isEqualTo(mockEventDomain);
        verify(mockEventDomain).openEvent();
        verify(eventRepository).save(mockEventDomain);
    }

    @Test
    @DisplayName("Event 상태를 CLOSED로 변경 성공")
    void changeStatus_ToClosed_Success() {
        // given
        Long eventId = 1L;
        String eventStatus = "CLOSED";
        
        EventDomain mockEventDomain = mock(EventDomain.class);
        given(eventRepository.findById(eventId)).willReturn(mockEventDomain);
        given(eventRepository.save(mockEventDomain)).willReturn(mockEventDomain);
        
        // when
        EventDomain result = eventCommandService.changeStatus(eventId, eventStatus);
        
        // then
        assertThat(result).isEqualTo(mockEventDomain);
        verify(mockEventDomain).closeEvent();
        verify(eventRepository).save(mockEventDomain);
    }

    @Test
    @DisplayName("유효하지 않은 Event 상태로 변경 시 예외 발생")
    void changeStatus_InvalidStatus_ThrowsException() {
        // given
        Long eventId = 1L;
        String invalidStatus = "INVALID_STATUS";
        
        EventDomain mockEventDomain = mock(EventDomain.class);
        given(eventRepository.findById(eventId)).willReturn(mockEventDomain);
        
        // when & then
        assertThrows(InvalidEventStatusException.class, 
                   () -> eventCommandService.changeStatus(eventId, invalidStatus));
        
        verify(mockEventDomain, never()).openEvent();
        verify(mockEventDomain, never()).closeEvent();
        verify(eventRepository, never()).save(mockEventDomain);
    }
    
    @Test
    @DisplayName("존재하지 않는 Event 상태 변경 시 예외 발생")
    void changeStatus_EventNotFound_ThrowsException() {
        // given
        Long eventId = 1L;
        String eventStatus = "OPEN";
        
        given(eventRepository.findById(eventId)).willReturn(null);
        
        // when & then
        assertThrows(EventNotFoundException.class, 
                   () -> eventCommandService.changeStatus(eventId, eventStatus));
        
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("전날 Event 종료 처리 성공")
    void closeYesterdayEvents_Success() {
        // given
        LocalDate yesterday = LocalDate.now().minusDays(1);
        
        EventDomain event1 = mock(EventDomain.class);
        EventDomain event2 = mock(EventDomain.class);
        when(event1.getId()).thenReturn(1L);
        when(event2.getId()).thenReturn(2L);
        
        List<EventDomain> yesterdayEvents = Arrays.asList(event1, event2);
        
        given(eventRepository.findByEventDateAndStatusNotClosed(yesterday)).willReturn(yesterdayEvents);
        
        when(eventRepository.findById(1L)).thenReturn(event1);
        when(eventRepository.findById(2L)).thenReturn(event2);
        when(eventRepository.save(any(EventDomain.class))).thenAnswer(i -> i.getArgument(0));
        
        // when
        eventCommandService.closeYesterdayEvents();
        
        // then
        verify(eventRepository).findByEventDateAndStatusNotClosed(yesterday);
        verify(event1).closeEvent();
        verify(event2).closeEvent();
        verify(eventRepository, times(2)).save(any(EventDomain.class));
    }

    @Test
    @DisplayName("전날 Event가 없을 경우 예외 발생")
    void closeYesterdayEvents_NoEventFound_ThrowsException() {
        // given
        LocalDate yesterday = LocalDate.now().minusDays(1);
        
        given(eventRepository.findByEventDateAndStatusNotClosed(yesterday)).willReturn(List.of());
        
        // when & then
        assertThrows(EventNotFoundException.class, () -> eventCommandService.closeYesterdayEvents());
        
        verify(eventRepository).findByEventDateAndStatusNotClosed(yesterday);
        verify(eventRepository, never()).save(any());
    }
}
