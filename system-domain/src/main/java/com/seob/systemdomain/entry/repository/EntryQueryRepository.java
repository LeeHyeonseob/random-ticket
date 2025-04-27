package com.seob.systemdomain.entry.repository;

import com.seob.systemdomain.entry.dto.EntryInfo;

import java.util.List;

/**
 * 이벤트 참여 조회 레포지토리
 */
public interface EntryQueryRepository {

    /**
     * 사용자 ID로 참여 내역 조회
     * @param userId 사용자 ID
     * @return 참여 내역 목록
     */
    List<EntryInfo> findByUserId(String userId);
    
    /**
     * 이벤트 ID로 참여 내역 조회
     * @param eventId 이벤트 ID
     * @return 참여 내역 목록
     */
    List<EntryInfo> findByEventId(Long eventId);
    
    /**
     * 특정 ID로 참여 내역 조회
     * @param entryId 참여 ID
     * @return 참여 내역
     */
    EntryInfo findById(Long entryId);
}
