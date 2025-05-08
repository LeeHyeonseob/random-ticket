package com.seob.systeminfra.event.service;

import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.event.service.EventValidationService;
import com.seob.systemdomain.event.vo.EventStatus;
import com.seob.systeminfra.event.exception.EventNotFoundException;
import com.seob.systeminfra.event.exception.InvalidEventStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EventValidationServiceImpl implements EventValidationService {

    private final EventRepository eventRepository;
    
    @Override
    public void validateEventExists(Long eventId) {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID는 null일 수 없습니다");
        }
        
        if (!isValidEventId(eventId)) {
            log.warn("존재하지 않는 Event ID: {}", eventId);
            throw EventNotFoundException.EXCEPTION;
        }
    }
    
    @Override
    public void validateEventStatus(Long eventId, EventStatus expectedStatus) {
        // Event 존재 여부 먼저 검증
        validateEventExists(eventId);
        
        // 상태 검증
        EventStatus currentStatus = eventRepository.findStatusById(eventId);
        if (currentStatus != expectedStatus) {
            log.warn("유효하지 않은 Event 상태. 예상: {}, 현재: {}", expectedStatus, currentStatus);
            throw InvalidEventStatusException.EXCEPTION;
        }
    }
    
    @Override
    public void validateEventDate(LocalDate eventDate) {
        if (eventDate == null) {
            throw new IllegalArgumentException("Event 날짜는 null일 수 없습니다");
        }
        
        LocalDate today = LocalDate.now();
        if (eventDate.isBefore(today)) {
            log.warn("유효하지 않은 Event 날짜: {}는 과거입니다", eventDate);
            throw new IllegalArgumentException("Event 날짜는 과거일 수 없습니다");
        }
    }
    
    @Override
    public boolean isValidEventId(Long eventId) {
        if (eventId == null) {
            return false;
        }
        
        EventDomain event = eventRepository.findById(eventId);
        return event != null;
    }
}
