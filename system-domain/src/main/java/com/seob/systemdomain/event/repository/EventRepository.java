package com.seob.systemdomain.event.repository;

import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.dto.EventDisplayInfo;
import com.seob.systemdomain.event.vo.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository {
    EventDomain save(EventDomain eventDomain);

    EventDomain findById(Long id);

    List<EventDomain> findAll();

    boolean existsById(Long id);

    EventStatus findStatusById(Long id);

    Long findIdByDate(LocalDate date);

    EventDisplayInfo findDisplayInfoById(Long id);

    List<EventDisplayInfo> findAllDisplayInfo();

    Page<EventDomain> findAllWithFilters(String status, LocalDate fromDate, LocalDate toDate, Pageable pageable);
    
    List<EventDomain> findByEventDateAndStatusNotClosed(LocalDate date);
}
