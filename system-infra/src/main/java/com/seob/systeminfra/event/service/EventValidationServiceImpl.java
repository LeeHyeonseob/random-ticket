package com.seob.systeminfra.event.service;

import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.event.service.EventValidationService;
import com.seob.systemdomain.event.vo.EventStatus;
import com.seob.systeminfra.event.exception.EventDataAccessException;
import com.seob.systeminfra.exception.EventNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;


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

        eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 Event ID: {}", eventId);
                    return EventNotFoundException.EXCEPTION;
                });
    }
    
    @Override
    public void validateEventStatus(Long eventId, EventStatus expectedStatus) {
        // Event 존재 여부 먼저 검증
        validateEventExists(eventId);
        
        // 상태 검증
        EventStatus currentStatus = eventRepository.findStatusById(eventId);
        if (currentStatus != expectedStatus) {
            log.warn("유효하지 않은 Event 상태. 예상: {}, 현재: {}", expectedStatus, currentStatus);
            throw new IllegalArgumentException("Invalid event status");
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
    

}
