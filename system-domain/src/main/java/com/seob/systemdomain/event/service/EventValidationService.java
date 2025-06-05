package com.seob.systemdomain.event.service;


import com.seob.systemdomain.event.vo.EventStatus;

public interface EventValidationService {

    //이벤트 여부 검증
    void validateEventExists(Long eventId);
    
    // 이벤트 상태 검증
    void validateEventStatus(Long eventId, EventStatus expectedStatus);
    
    // 날짜 검증
    void validateEventDate(java.time.LocalDate eventDate);

}
