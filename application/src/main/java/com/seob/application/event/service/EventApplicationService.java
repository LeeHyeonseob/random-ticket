package com.seob.application.event.service;

import com.seob.application.event.dto.EventCreateRequestDto;
import com.seob.application.event.dto.EventResponseDto;
import com.seob.application.event.dto.EventStatusUpdateRequestDto;
import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.dto.EventDisplayInfo;
import com.seob.systemdomain.event.exception.EventNotFoundException;
import com.seob.systemdomain.event.service.EventService;
import com.seob.systemdomain.event.vo.EventStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 이벤트 애플리케이션 서비스
 * 사용자 요청 처리 및 도메인 서비스 조율
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventApplicationService {
    private final EventService eventService;
    
    /**
     * 이벤트 생성 요청 처리
     */
    @Transactional
    public EventResponseDto createEvent(EventCreateRequestDto requestDto) {
        log.info("Creating new event: {}", requestDto.getName());
        
        // 도메인 객체 생성 - 직접 도메인 모델의 팩토리 메서드 호출
        EventDomain eventDomain = EventDomain.create(
            requestDto.getName(),
            requestDto.getDescription(),
            requestDto.getEventDate()
        );
        
        // 저장 처리는 인프라 계층 서비스에 위임
        EventDomain savedEvent = eventService.createEvent(
            eventDomain.getName(),
            eventDomain.getDescription(),
            eventDomain.getEventDate()
        );
        
        log.info("Event created with ID: {}", savedEvent.getId());
        return convertToResponseDto(savedEvent);
    }
    
    /**
     * 이벤트 상태 변경 요청 처리
     */
    @Transactional
    public EventResponseDto updateEventStatus(Long eventId, EventStatusUpdateRequestDto requestDto) {
        log.info("Updating event status. Event ID: {}, New status: {}", eventId, requestDto.getStatus());
        
        EventDomain event = eventService.findById(eventId);
        if (event == null) {
            throw EventNotFoundException.EXCEPTION;
        }
        
        EventStatus newStatus = EventStatus.valueOf(requestDto.getStatus().toUpperCase());
        
        // 도메인 객체에 상태 변경 위임
        event.changeStatus(newStatus);
        
        // 변경사항 저장
        EventDomain updatedEvent = eventService.changeStatus(eventId, newStatus.name());
        log.info("Event status updated to: {}", updatedEvent.getStatus());
        
        return convertToResponseDto(updatedEvent);
    }
    
    /**
     * 이벤트 목록 조회 요청 처리
     */
    @Transactional(readOnly = true)
    public List<EventResponseDto> getAllEvents() {
        log.info("Retrieving all events");
        List<EventDomain> events = eventService.findAll();
        
        return events.stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
    }
    
    /**
     * 이벤트 상세 정보 조회 요청 처리
     */
    @Transactional(readOnly = true)
    public EventResponseDto getEventById(Long eventId) {
        log.info("Retrieving event details. Event ID: {}", eventId);
        
        EventDomain event = eventService.findById(eventId);
        if (event == null) {
            throw EventNotFoundException.EXCEPTION;
        }
        
        return convertToResponseDto(event);
    }
    
    /**
     * 이벤트 표시 정보 조회
     */
    @Transactional(readOnly = true)
    public EventDisplayInfo getEventDisplayInfo(Long eventId) {
        log.info("Retrieving event display info. Event ID: {}", eventId);
        return eventService.getEventDisplayInfo(eventId);
    }
    
    /**
     * 어제 날짜 이벤트 자동 종료 처리
     */
    @Transactional
    public void closeYesterdayEvents() {
        log.info("Processing automatic closing of yesterday's events");
        eventService.closeYesterdayEvents();
        log.info("Completed processing yesterday's events");
    }
    
    /**
     * 도메인 객체를 응답 DTO로 변환
     */
    private EventResponseDto convertToResponseDto(EventDomain event) {
        return EventResponseDto.builder()
            .id(event.getId())
            .name(event.getName())
            .description(event.getDescription())
            .status(event.getStatus().name())
            .eventDate(event.getEventDate())
            .createdAt(event.getCreatedAt())
            .build();
    }
}
