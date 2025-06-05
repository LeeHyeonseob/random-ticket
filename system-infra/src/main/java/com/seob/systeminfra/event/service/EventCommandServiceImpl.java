package com.seob.systeminfra.event.service;

import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.event.service.EventCommandService;
import com.seob.systemdomain.event.vo.EventStatus;
import com.seob.systeminfra.event.exception.EventDataAccessException;
import com.seob.systeminfra.exception.EventNotFoundException;
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
        validateEventDate(eventDate);
        
        EventDomain createdEvent = EventDomain.create(name, description, eventDate);
        return eventRepository.save(createdEvent);
    }
    
    @Override
    public EventDomain changeStatus(Long eventId, String eventStatus) {

        EventDomain event = eventRepository.findById(eventId)
                .orElseThrow(() -> EventNotFoundException.EXCEPTION);

        EventStatus newStatus = parseEventStatus(eventStatus);
        event.changeStatus(newStatus);
        
        return eventRepository.save(event);
    }
    
    @Override
    public void closeYesterdayEvents() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        
        List<EventDomain> events = eventRepository.findByEventDateAndStatusNotClosed(yesterday);
        if(events.isEmpty()) {
            throw EventDataAccessException.EXCEPTION;
        }
        
        for (EventDomain event : events) {

            event.changeStatus(EventStatus.CLOSED);
            eventRepository.save(event);
        }
    }
    
    private EventStatus parseEventStatus(String eventStatus) {
        return switch (eventStatus) {
            case "OPEN" -> EventStatus.OPEN;
            case "CLOSED" -> EventStatus.CLOSED;
            case "SCHEDULED" -> EventStatus.SCHEDULED;
            default -> throw new IllegalArgumentException("Invalid event status: " + eventStatus);
        };
    }
    
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
