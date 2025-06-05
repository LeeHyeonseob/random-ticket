package com.seob.systeminfra.event.service;

import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.dto.EventDisplayInfo;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.event.service.EventQueryService;
import com.seob.systeminfra.exception.EventNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EventQueryServiceImpl implements EventQueryService {

    private final EventRepository eventRepository;
    
    @Override
    public EventDomain findById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> EventNotFoundException.EXCEPTION);
    }
    
    @Override
    public List<EventDomain> findAll() {
        return eventRepository.findAll();
    }
    
    @Override
    public Page<EventDomain> findAllWithFilters(String status, LocalDate fromDate, 
                                              LocalDate toDate, Pageable pageable) {
        return eventRepository.findAllWithFilters(status, fromDate, toDate, pageable);
    }
    
    @Override
    public EventDisplayInfo findDisplayInfoById(Long eventId) {
        return eventRepository.findDisplayInfoById(eventId);
    }
    
    @Override
    public List<EventDisplayInfo> findAllDisplayInfo() {
        return eventRepository.findAllDisplayInfo();
    }
}
