package com.seob.systemdomain.event.service;

import com.seob.systemdomain.event.domain.EventDomain;

import java.time.LocalDate;
import java.util.List;

public interface EventService {
    EventDomain createEvent(String name, String description, LocalDate eventDate);
    EventDomain findById(Long eventId);

    List<EventDomain> findAll();

    EventDomain changeStatus(Long eventId, String eventStatus);
}
