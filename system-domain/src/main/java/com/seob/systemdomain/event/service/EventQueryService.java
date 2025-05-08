package com.seob.systemdomain.event.service;

import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.dto.EventDisplayInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;


public interface EventQueryService {

    EventDomain findById(Long eventId);
    

    List<EventDomain> findAll();
    
    //페이징된 이벤트 조회
    Page<EventDomain> findAllWithFilters(String status, LocalDate fromDate, 
                                         LocalDate toDate, Pageable pageable);
    
    // 사용자를 위한 특정 이벤트 조회
    EventDisplayInfo findDisplayInfoById(Long eventId);
    
    // 사용자를 위한 전체 이벤트 조회
    List<EventDisplayInfo> findAllDisplayInfo();
}
