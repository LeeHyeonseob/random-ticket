package com.seob.systemdomain.entry.service;

import com.seob.systemdomain.entry.dto.ParticipantInfo;
import com.seob.systemdomain.entry.dto.UserEventInfo;

import java.util.List;

// 이벤트 참여 조회 관련 서비스
public interface EntryQueryService {
    // 이벤트별 참가자 상세 조회
    List<ParticipantInfo> findParticipantDetailsByEventId(Long eventId);
    
    // 사용자별 이벤트 참여 정보 조회
    List<UserEventInfo> findUserEventInfoByUserId(String userId);
}
