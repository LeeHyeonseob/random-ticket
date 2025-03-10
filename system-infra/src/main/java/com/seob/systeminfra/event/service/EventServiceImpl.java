package com.seob.systeminfra.event.service;

import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.dto.EventDisplayInfo;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.event.service.EventService;
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
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;


    @Override
    public EventDomain createEvent(String name, String description, LocalDate eventDate) {
        EventDomain createdEvent = EventDomain.create(name, description, eventDate);
        return eventRepository.save(createdEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public EventDomain findById(Long eventId) {
        return eventRepository.findById(eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDomain> findAll() {
        return eventRepository.findAll();
    }

    @Override
    public EventDomain changeStatus(Long eventId, String eventStatus) {
        EventDomain findEvent = findById(eventId);
        log.info( "event Status : {}", eventStatus );
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
    @Transactional(readOnly = true)
    public EventDisplayInfo getEventDisplayInfo(Long eventId) {
        return eventRepository.findDisplayInfoById(eventId);
    }

    @Override
    public List<EventDisplayInfo> getEventDisplayInfoList() {
        return eventRepository.findAllDisplayInfo();
    }
}
