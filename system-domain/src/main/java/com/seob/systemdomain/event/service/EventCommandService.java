package com.seob.systemdomain.event.service;

import com.seob.systemdomain.event.domain.EventDomain;

import java.time.LocalDate;


public interface EventCommandService {
    // 이벤트 생성
    EventDomain createEvent(String name, String description, LocalDate eventDate);
    
    // 이벤트 상태 변경
    EventDomain changeStatus(Long eventId, String eventStatus);
    
    // 어제 이벤트 종료 처리
    void closeYesterdayEvents();
}
