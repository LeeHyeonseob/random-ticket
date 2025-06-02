package com.seob.application.entry.service;

import com.seob.application.entry.controller.dto.EntryResponse;
import com.seob.application.entry.controller.dto.ParticipantEntryResponse;
import com.seob.application.entry.controller.dto.UserEntryResponse;
import com.seob.systemdomain.user.domain.vo.UserId;

import java.util.List;

// 이벤트 참여 애플리케이션 서비스
public interface EntryApplicationService {
    // 이벤트 참여 신청 (티켓 ID 직접 제공)
    EntryResponse applyToEvent(Long eventId, String ticketId, UserId userId);
    
    // 이벤트 참여 신청 (티켓 ID 자동 찾기)
    EntryResponse applyToEventWithoutTicket(Long eventId, UserId userId);
    
    // 사용자의 이벤트 참여 내역 조회
    List<UserEntryResponse> getMyEntries(UserId userId);
    
    // 특정 사용자의 이벤트 참여 내역 조회 (관리자용)
    List<UserEntryResponse> getUserEntries(String userId);
    
    // 특정 이벤트의 참여 내역 조회 (관리자용)
    List<ParticipantEntryResponse> getEventEntries(Long eventId);
}
