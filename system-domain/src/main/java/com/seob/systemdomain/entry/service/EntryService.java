package com.seob.systemdomain.entry.service;

import com.seob.systemdomain.entry.domain.EntryDomain;

// 이벤트 참여 관련 핵심 비즈니스 로직
public interface EntryService {
    // 이벤트 참여 신청 (티켓 ID 직접 제공)
    EntryDomain apply(String userId, Long eventId, String ticketId);
    
    // 이벤트 참여 신청 (티켓 ID 자동 찾기)
    EntryDomain applyWithoutTicketId(String userId, Long eventId);
}
