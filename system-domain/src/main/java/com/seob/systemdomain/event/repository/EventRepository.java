package com.seob.systemdomain.event.repository;

import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.dto.EventDisplayInfo;
import com.seob.systemdomain.event.vo.EventStatus;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository {
    EventDomain save(EventDomain eventDomain);

    EventDomain findById(Long id);

    List<EventDomain> findAll();

    EventStatus findStatusById(Long id);

    Long findIdByDate(LocalDate date);

    EventDisplayInfo findDisplayInfoById(Long id);

    List<EventDisplayInfo> findAllDisplayInfo();
}
