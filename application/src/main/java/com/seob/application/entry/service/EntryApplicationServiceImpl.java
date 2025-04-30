package com.seob.application.entry.service;

import com.seob.application.common.utils.SecurityUtils;
import com.seob.application.entry.controller.dto.EntryResponse;
import com.seob.application.entry.controller.dto.ParticipantEntryResponse;
import com.seob.application.entry.controller.dto.UserEntryResponse;
import com.seob.systemdomain.entry.domain.EntryDomain;
import com.seob.systemdomain.entry.dto.ParticipantInfo;
import com.seob.systemdomain.entry.dto.UserEventInfo;
import com.seob.systemdomain.entry.service.EntryQueryService;
import com.seob.systemdomain.entry.service.EntryService;
import com.seob.systemdomain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EntryApplicationServiceImpl implements EntryApplicationService {

    private final EntryService entryService;
    private final EntryQueryService entryQueryService;
    private final EventRepository eventRepository;
    
    @Override
    public EntryResponse applyToEvent(Long eventId, String ticketId) {
        // 현재 사용자 ID 가져오기
        String currentUserId = SecurityUtils.getCurrentUserId();
        
        // 이벤트 참여 처리
        EntryDomain entry = entryService.apply(currentUserId, eventId, ticketId);
        
        // 이벤트 이름 조회
        String eventName = eventRepository.findById(eventId).getName();
        
        // 응답 변환
        return new EntryResponse(
                entry.getId(),
                entry.getEventId(),
                eventName,
                entry.getTicketId(),
                entry.getCreatedAt()
        );
    }
    
    @Override
    public EntryResponse applyToEventWithoutTicket(Long eventId) {
        // 현재 사용자 ID 가져오기
        String currentUserId = SecurityUtils.getCurrentUserId();
        
        // 티켓 ID 없이 이벤트 참여 처리
        EntryDomain entry = entryService.applyWithoutTicketId(currentUserId, eventId);
        
        // 이벤트 이름 조회
        String eventName = eventRepository.findById(eventId).getName();
        
        // 응답 변환
        return new EntryResponse(
                entry.getId(),
                entry.getEventId(),
                eventName,
                entry.getTicketId(),
                entry.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntryResponse> getMyEntries() {
        String currentUserId = SecurityUtils.getCurrentUserId();
        List<UserEventInfo> userEvents = entryQueryService.findUserEventInfoByUserId(currentUserId);
        
        return userEvents.stream()
                .map(UserEntryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntryResponse> getUserEntries(String userId) {
        // 관리자 권한 체크
        if (!SecurityUtils.isAdmin()) {
            throw new AccessDeniedException("관리자 권한이 필요합니다");
        }
        
        List<UserEventInfo> userEvents = entryQueryService.findUserEventInfoByUserId(userId);
        
        return userEvents.stream()
                .map(UserEntryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipantEntryResponse> getEventEntries(Long eventId) {
        // 관리자 권한 체크
        if (!SecurityUtils.isAdmin()) {
            throw new AccessDeniedException("관리자 권한이 필요합니다");
        }
        
        // 이벤트 이름 조회
        String eventName = eventRepository.findById(eventId).getName();
        
        List<ParticipantInfo> participants = entryQueryService.findParticipantDetailsByEventId(eventId);
        
        return participants.stream()
                .map(info -> ParticipantEntryResponse.from(info, eventName))
                .collect(Collectors.toList());
    }
}
