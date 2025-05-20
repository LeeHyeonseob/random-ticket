package com.seob.systeminfra.event.service;

import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.dto.EventDisplayInfo;
import com.seob.systemdomain.event.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventQueryServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventQueryServiceImpl eventQueryService;

    @Test
    @DisplayName("ID로 Event 조회 성공")
    void findById_Success() {
        // given
        Long eventId = 1L;
        EventDomain mockEventDomain = mock(EventDomain.class);
        
        given(eventRepository.findById(eventId)).willReturn(mockEventDomain);
        
        // when
        EventDomain result = eventQueryService.findById(eventId);
        
        // then
        assertThat(result).isEqualTo(mockEventDomain);
        verify(eventRepository).findById(eventId);
    }

    @Test
    @DisplayName("모든 Event 조회 성공")
    void findAll_Success() {
        // given
        EventDomain event1 = mock(EventDomain.class);
        EventDomain event2 = mock(EventDomain.class);
        List<EventDomain> expectedEvents = Arrays.asList(event1, event2);
        
        given(eventRepository.findAll()).willReturn(expectedEvents);
        
        // when
        List<EventDomain> result = eventQueryService.findAll();
        
        // then
        assertThat(result).isEqualTo(expectedEvents);
        verify(eventRepository).findAll();
    }
    
    @Test
    @DisplayName("필터링된 Event 목록 조회 성공")
    void findAllWithFilters_Success() {
        // given
        String status = "OPEN";
        LocalDate fromDate = LocalDate.of(2025, 1, 1);
        LocalDate toDate = LocalDate.of(2025, 12, 31);
        Pageable pageable = PageRequest.of(0, 10);
        
        EventDomain event1 = mock(EventDomain.class);
        EventDomain event2 = mock(EventDomain.class);
        List<EventDomain> eventList = Arrays.asList(event1, event2);
        Page<EventDomain> eventPage = new PageImpl<>(eventList, pageable, eventList.size());
        
        given(eventRepository.findAllWithFilters(status, fromDate, toDate, pageable)).willReturn(eventPage);
        
        // when
        Page<EventDomain> result = eventQueryService.findAllWithFilters(status, fromDate, toDate, pageable);
        
        // then
        assertThat(result).isEqualTo(eventPage);
        assertThat(result.getContent()).isEqualTo(eventList);
        verify(eventRepository).findAllWithFilters(status, fromDate, toDate, pageable);
    }

    @Test
    @DisplayName("Event 표시 정보 조회 성공")
    void findDisplayInfoById_Success() {
        // given
        Long eventId = 1L;
        EventDisplayInfo mockDisplayInfo = mock(EventDisplayInfo.class);
        
        given(eventRepository.findDisplayInfoById(eventId)).willReturn(mockDisplayInfo);
        
        // when
        EventDisplayInfo result = eventQueryService.findDisplayInfoById(eventId);
        
        // then
        assertThat(result).isEqualTo(mockDisplayInfo);
        verify(eventRepository).findDisplayInfoById(eventId);
    }

    @Test
    @DisplayName("모든 Event 표시 정보 조회 성공")
    void findAllDisplayInfo_Success() {
        // given
        EventDisplayInfo info1 = mock(EventDisplayInfo.class);
        EventDisplayInfo info2 = mock(EventDisplayInfo.class);
        List<EventDisplayInfo> expectedInfos = Arrays.asList(info1, info2);
        
        given(eventRepository.findAllDisplayInfo()).willReturn(expectedInfos);
        
        // when
        List<EventDisplayInfo> result = eventQueryService.findAllDisplayInfo();
        
        // then
        assertThat(result).isEqualTo(expectedInfos);
        verify(eventRepository).findAllDisplayInfo();
    }
}
