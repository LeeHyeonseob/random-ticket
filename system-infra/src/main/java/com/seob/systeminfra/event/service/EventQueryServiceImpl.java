package com.seob.systeminfra.event.service;

import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.dto.EventDisplayInfo;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.event.service.EventQueryService;
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
        log.debug("ID로 Event 조회: {}", eventId);
        return eventRepository.findById(eventId);
    }
    
    @Override
    public List<EventDomain> findAll() {
        log.debug("모든 Event 조회");
        return eventRepository.findAll();
    }
    
    @Override
    public Page<EventDomain> findAllWithFilters(String status, LocalDate fromDate, 
                                              LocalDate toDate, Pageable pageable) {
        log.debug("필터를 적용한 Event 조회 - 상태: {}, 시작일: {}, 종료일: {}", 
                 status, fromDate, toDate);
        return eventRepository.findAllWithFilters(status, fromDate, toDate, pageable);
    }
    
    @Override
    public EventDisplayInfo findDisplayInfoById(Long eventId) {
        log.debug("ID로 Event 표시 정보 조회: {}", eventId);
        return eventRepository.findDisplayInfoById(eventId);
    }
    
    @Override
    public List<EventDisplayInfo> findAllDisplayInfo() {
        log.debug("모든 Event 표시 정보 조회");
        return eventRepository.findAllDisplayInfo();
    }
}
