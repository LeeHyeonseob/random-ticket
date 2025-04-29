package com.seob.systemdomain.entry.repository;

import com.seob.systemdomain.entry.domain.EntryDomain;
import com.seob.systemdomain.entry.dto.ParticipantInfo;
import com.seob.systemdomain.entry.dto.UserEventInfo;

import java.util.List;

// 이벤트 참여 관련 저장소
public interface EntryRepository {
    // EntryDomain 저장
    EntryDomain save(EntryDomain entryDomain);
    
    // 이벤트별 참여자 ID 목록 조회
    List<String> findUserIdByEventId(Long eventId);
    
    // 이벤트별 참여자 상세 정보 조회
    List<ParticipantInfo> findParticipantDetailsByEventId(Long eventId);
    
    // 사용자별 이벤트 참여 정보 조회
    List<UserEventInfo> findUserEventInfoByUserId(String userId);
}
