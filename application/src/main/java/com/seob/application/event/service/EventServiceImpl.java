package com.seob.application.event.service;

import com.seob.application.event.exception.InvalidEventStatusException;
import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.dto.EventDisplayInfo;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
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
