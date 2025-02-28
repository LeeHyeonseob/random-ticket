package com.seob.systemdomain.event.repository;

import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.vo.EventStatus;

public interface EventRepository {
    EventDomain save(EventDomain eventDomain);

    EventDomain findById(Long id);

    EventStatus findStatusById(Long id);
}
