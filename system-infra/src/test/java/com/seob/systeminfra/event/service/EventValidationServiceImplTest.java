package com.seob.systeminfra.event.service;

import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.event.service.EventValidationService;
import com.seob.systemdomain.event.vo.EventStatus;
import com.seob.systeminfra.event.exception.EventNotFoundException;
import com.seob.systeminfra.event.exception.InvalidEventStatusException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventValidationServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventValidationServiceImpl eventValidationService;

    @Test
    @DisplayName("Event 존재 여부 검증 성공")
    void validateEventExists_Success() {
        // given
        Long eventId = 1L;
        EventDomain mockEventDomain = mock(EventDomain.class);
        
        given(eventRepository.findById(eventId)).willReturn(mockEventDomain);
        
        // when & then
        assertDoesNotThrow(() -> eventValidationService.validateEventExists(eventId));
        verify(eventRepository).findById(eventId);
    }

    @Test
    @DisplayName("존재하지 않는 Event일 경우 예외 발생")
    void validateEventExists_NotFound_ThrowsException() {
        // given
        Long eventId = 1L;
        given(eventRepository.findById(eventId)).willReturn(null);
        
        // when & then
        assertThrows(EventNotFoundException.class, () -> eventValidationService.validateEventExists(eventId));
        verify(eventRepository).findById(eventId);
    }

    @Test
    @DisplayName("null ID로 검증 시 예외 발생")
    void validateEventExists_NullId_ThrowsException() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> eventValidationService.validateEventExists(null));
    }

    @Test
    @DisplayName("Event 상태 검증 성공")
    void validateEventStatus_Success() {
        // given
        Long eventId = 1L;
        EventStatus expectedStatus = EventStatus.OPEN;
        EventDomain mockEventDomain = mock(EventDomain.class);
        
        given(eventRepository.findById(eventId)).willReturn(mockEventDomain);
        given(eventRepository.findStatusById(eventId)).willReturn(expectedStatus);
        
        // when & then
        assertDoesNotThrow(() -> eventValidationService.validateEventStatus(eventId, expectedStatus));
        verify(eventRepository).findStatusById(eventId);
    }

    @Test
    @DisplayName("Event 상태가 예상과 다를 경우 예외 발생")
    void validateEventStatus_Mismatch_ThrowsException() {
        // given
        Long eventId = 1L;
        EventStatus expectedStatus = EventStatus.OPEN;
        EventStatus actualStatus = EventStatus.CLOSED;
        EventDomain mockEventDomain = mock(EventDomain.class);
        
        given(eventRepository.findById(eventId)).willReturn(mockEventDomain);
        given(eventRepository.findStatusById(eventId)).willReturn(actualStatus);
        
        // when & then
        assertThrows(InvalidEventStatusException.class, 
                     () -> eventValidationService.validateEventStatus(eventId, expectedStatus));
        verify(eventRepository).findStatusById(eventId);
    }

    @Test
    @DisplayName("Event 날짜 검증 성공")
    void validateEventDate_Success() {
        // given
        LocalDate futureDate = LocalDate.now().plusDays(1);
        
        // when & then
        assertDoesNotThrow(() -> eventValidationService.validateEventDate(futureDate));
    }

    @Test
    @DisplayName("과거 날짜 검증 시 예외 발생")
    void validateEventDate_PastDate_ThrowsException() {
        // given
        LocalDate pastDate = LocalDate.now().minusDays(1);
        
        // when & then
        assertThrows(IllegalArgumentException.class, () -> eventValidationService.validateEventDate(pastDate));
    }

    @Test
    @DisplayName("null 날짜 검증 시 예외 발생")
    void validateEventDate_NullDate_ThrowsException() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> eventValidationService.validateEventDate(null));
    }
}
