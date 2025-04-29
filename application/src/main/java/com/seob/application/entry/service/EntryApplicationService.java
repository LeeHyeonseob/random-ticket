package com.seob.application.entry.service;

import com.seob.application.entry.controller.dto.EntryResponse;

import java.util.List;

// 이벤트 참여 애플리케이션 서비스
public interface EntryApplicationService {
    // 이벤트 참여 신청
    EntryResponse applyToEvent(Long eventId, String ticketId);
    
    // 내 이벤트 참여 내역 조회
    List<EntryResponse> getMyEntries();
    
    // 특정 사용자의 이벤트 참여 내역 조회 (관리자용)
    List<EntryResponse> getUserEntries(String userId);
    
    // 특정 이벤트의 참여 내역 조회 (관리자용)
    List<EntryResponse> getEventEntries(Long eventId);
}
