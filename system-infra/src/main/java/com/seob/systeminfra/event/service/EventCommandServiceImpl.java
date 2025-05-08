package com.seob.systeminfra.event.service;

import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.event.service.EventCommandService;
import com.seob.systeminfra.event.exception.EventNotFoundException;
import com.seob.systeminfra.event.exception.InvalidEventStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EventCommandServiceImpl implements EventCommandService {
    
    private final EventRepository eventRepository;
    
    @Override
    public EventDomain createEvent(String name, String description, LocalDate eventDate) {
        // 날짜 유효성 검증
        validateEventDate(eventDate);
        
        // 도메인 객체 생성
        EventDomain createdEvent = EventDomain.create(name, description, eventDate);
        return eventRepository.save(createdEvent);
    }
    
    @Override
    public EventDomain changeStatus(Long eventId, String eventStatus) {
        // Event 존재 여부 검증
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID는 null일 수 없습니다");
        }
        
        // Event 조회
        EventDomain findEvent = eventRepository.findById(eventId);
        if (findEvent == null) {
            throw EventNotFoundException.EXCEPTION;
        }
        
        log.info("Event 상태 변경 요청: {}", eventStatus);
        
        // 상태 변경 처리
        return switch (eventStatus) {
            case "OPEN" -> {
                findEvent.openEvent();
                yield eventRepository.save(findEvent);
            }
            case "CLOSED" -> {
                findEvent.closeEvent();
                yield eventRepository.save(findEvent);
            }
            default -> throw InvalidEventStatusException.EXCEPTION;
        };
    }
    
    @Override
    public void closeYesterdayEvents() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        
        // 어제 열린 Event 중 아직 CLOSED 되지 않은 Event 찾기
        List<EventDomain> events = eventRepository.findByEventDateAndStatusNotClosed(yesterday);
        if(events.isEmpty()) {
            throw EventNotFoundException.EXCEPTION;
        }
        
        // 모든 Event를 닫음
        for (EventDomain event : events) {
            changeStatus(event.getId(), "CLOSED");
        }
    }
    
    // 내부 검증 메서드
    private void validateEventDate(LocalDate eventDate) {
        if (eventDate == null) {
            throw new IllegalArgumentException("Event 날짜는 null일 수 없습니다");
        }
        
        LocalDate today = LocalDate.now();
        if (eventDate.isBefore(today)) {
            log.warn("유효하지 않은 Event 날짜: {}는 과거입니다", eventDate);
            throw new IllegalArgumentException("Event 날짜는 과거일 수 없습니다");
        }
    }
}
