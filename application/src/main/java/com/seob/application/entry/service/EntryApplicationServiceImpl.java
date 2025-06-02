package com.seob.application.entry.service;

import com.seob.application.entry.controller.dto.EntryResponse;
import com.seob.application.entry.controller.dto.ParticipantEntryResponse;
import com.seob.application.entry.controller.dto.UserEntryResponse;
import com.seob.systemdomain.entry.domain.EntryDomain;
import com.seob.systemdomain.entry.dto.ParticipantInfo;
import com.seob.systemdomain.entry.dto.UserEventInfo;
import com.seob.systemdomain.entry.service.EntryQueryService;
import com.seob.systemdomain.entry.service.EntryService;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.user.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
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
    public EntryResponse applyToEvent(Long eventId, String ticketId, UserId userId) {
        // 이벤트 참여 처리
        EntryDomain entry = entryService.apply(userId.getValue(), eventId, ticketId);
        
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
    public EntryResponse applyToEventWithoutTicket(Long eventId, UserId userId) {
        // 티켓 ID 없이 이벤트 참여 처리
        EntryDomain entry = entryService.applyWithoutTicketId(userId.getValue(), eventId);
        
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
    public List<UserEntryResponse> getMyEntries(UserId userId) {
        List<UserEventInfo> userEvents = entryQueryService.findUserEventInfoByUserId(userId.getValue());
        
        return userEvents.stream()
                .map(UserEntryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntryResponse> getUserEntries(String userId) {
        List<UserEventInfo> userEvents = entryQueryService.findUserEventInfoByUserId(userId);
        
        return userEvents.stream()
                .map(UserEntryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipantEntryResponse> getEventEntries(Long eventId) {
        // 이벤트 이름 조회
        String eventName = eventRepository.findById(eventId).getName();
        
        List<ParticipantInfo> participants = entryQueryService.findParticipantDetailsByEventId(eventId);
        
        return participants.stream()
                .map(info -> ParticipantEntryResponse.from(info, eventName))
                .collect(Collectors.toList());
    }
}
