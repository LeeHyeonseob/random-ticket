package com.seob.application.entry.service;

import com.seob.application.common.utils.SecurityUtils;
import com.seob.application.entry.controller.dto.EntryResponse;
import com.seob.systemdomain.entry.dto.EntryInfo;
import com.seob.systemdomain.entry.repository.EntryQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EntryApplicationServiceImpl implements EntryApplicationService {

    private final EntryQueryRepository entryQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EntryResponse> getMyEntries() {
        String currentUserId = SecurityUtils.getCurrentUserId();
        
        // 현재 사용자의 이벤트 참여 내역 조회
        List<EntryInfo> entries = entryQueryRepository.findByUserId(currentUserId);
        
        return entries.stream()
                .map(EntryResponse::of)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntryResponse> getUserEntries(String userId) {
        // 특정 사용자의 이벤트 참여 내역 조회 (관리자 전용)
        List<EntryInfo> entries = entryQueryRepository.findByUserId(userId);
        
        return entries.stream()
                .map(EntryResponse::of)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntryResponse> getEventEntries(Long eventId) {
        // 특정 이벤트의 참여 내역 조회 (관리자 전용)
        List<EntryInfo> entries = entryQueryRepository.findByEventId(eventId);
        
        return entries.stream()
                .map(EntryResponse::of)
                .toList();
    }
}
