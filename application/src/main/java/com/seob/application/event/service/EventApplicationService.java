package com.seob.application.event.service;

import com.seob.application.event.dto.EventCreateRequestDto;
import com.seob.application.event.dto.EventResponseDto;
import com.seob.application.event.dto.EventStatusUpdateRequestDto;
import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.dto.EventDisplayInfo;
import com.seob.systemdomain.event.service.EventCommandService;
import com.seob.systemdomain.event.service.EventQueryService;
import com.seob.systemdomain.event.service.EventValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventApplicationService {
    private final EventCommandService eventCommandService;
    private final EventQueryService eventQueryService;
    private final EventValidationService eventValidationService;
    
    // 이벤트 생성 요청 처리
    @Transactional
    public EventResponseDto createEvent(EventCreateRequestDto requestDto) {
        log.info("Event 생성: {}", requestDto.getName());
        
        // 도메인 명령 서비스에 이벤트 생성 위임
        EventDomain savedEvent = eventCommandService.createEvent(
            requestDto.getName(),
            requestDto.getDescription(),
            requestDto.getEventDate()
        );
        
        log.info("생성된 Event ID: {}", savedEvent.getId());
        return EventResponseDto.from(savedEvent);
    }
    
    // 상태 변경
    @Transactional
    public EventResponseDto updateEventStatus(Long eventId, EventStatusUpdateRequestDto requestDto) {
        log.info("이벤트 상태 변경. Event ID: {}, 변경된 상태: {}", eventId, requestDto.getStatus());
        
        // 이벤트 존재 여부 확인
        eventValidationService.validateEventExists(eventId);
        
        // 도메인 명령 서비스에 상태 변경 위임
        String statusUpperCase = requestDto.getStatus().toUpperCase();
        EventDomain updatedEvent = eventCommandService.changeStatus(eventId, statusUpperCase);
        
        log.info("Event 상태 변경 완료 : {}", updatedEvent.getStatus());
        return EventResponseDto.from(updatedEvent);
    }
    
    // 페이징된 이벤트 목록 조회
    @Transactional(readOnly = true)
    public Page<EventResponseDto> getAllEvents(String status, LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        log.info("Event 목록 조회 : {}, fromDate: {}, toDate: {}", status, fromDate, toDate);
        
        // 도메인 조회 서비스에 필터링된 조회 위임
        Page<EventDomain> eventPage = eventQueryService.findAllWithFilters(status, fromDate, toDate, pageable);
        
        // 도메인 객체를 DTO로 변환
        List<EventResponseDto> content = eventPage.getContent().stream()
            .map(EventResponseDto::from )
            .collect(Collectors.toList());
        
        log.info(" {}개 event (page {} of {}, total: {})",
                content.size(), pageable.getPageNumber() + 1, 
                eventPage.getTotalPages(), eventPage.getTotalElements());
        
        return new PageImpl<>(content, pageable, eventPage.getTotalElements());
    }
    
    // 이벤트 조회
    @Transactional(readOnly = true)
    public EventResponseDto getEventById(Long eventId) {
        log.info("관리자 이벤트 조회 Event ID: {}", eventId);
        
        // 이벤트 존재 여부 검증
        eventValidationService.validateEventExists(eventId);
        
        // 도메인 조회 서비스에 조회 위임
        EventDomain event = eventQueryService.findById(eventId);
        
        return EventResponseDto.from(event);
    }
    
    // 사용자 조회
    @Transactional(readOnly = true)
    public EventDisplayInfo getEventDisplayInfo(Long eventId) {
        log.info("사용자 이벤트 조회 Event ID: {}", eventId);
        
        // 이벤트 존재 여부 검증
        eventValidationService.validateEventExists(eventId);
        
        // 도메인 조회 서비스에 조회 위임
        return eventQueryService.findDisplayInfoById(eventId);
    }

}