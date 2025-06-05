package com.seob.application.entry.service;

import com.seob.application.entry.controller.dto.EntryResponse;
import com.seob.application.entry.controller.dto.ParticipantEntryResponse;
import com.seob.application.entry.controller.dto.UserEntryResponse;
import com.seob.systemdomain.entry.domain.EntryDomain;
import com.seob.systemdomain.entry.dto.ParticipantInfo;
import com.seob.systemdomain.entry.dto.UserEventInfo;
import com.seob.systemdomain.entry.service.EntryQueryService;
import com.seob.systemdomain.entry.service.EntryService;
import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.repository.TicketRepository;
import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systeminfra.entry.exception.EntryDataAccessException;
import com.seob.systeminfra.exception.EventNotFoundException;
import com.seob.systeminfra.exception.TicketNotFoundException;
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
    private final TicketRepository ticketRepository;

    
    @Override
    public EntryResponse applyToEvent(Long eventId, UserId userId) {
        try {
            // 1. 도메인 객체 조회
            EventDomain event = eventRepository.findById(eventId)
                    .orElseThrow(() -> EventNotFoundException.EXCEPTION);
            TicketDomain ticket = ticketRepository.findByUserIdAndEventIdAndNotUsed(userId, eventId)
                    .orElseThrow(() -> TicketNotFoundException.EXCEPTION);
            
            // 검증
            event.validateCanApply();
            ticket.validateCanUse();

            //티켓 사용
            ticket.use();
            
            //저장
            EntryDomain entry = entryService.apply(userId.getValue(), eventId);
            
            // 5. 응답 변환
            return new EntryResponse(
                    entry.getId(),
                    entry.getEventId(),
                    event.getName(),
                    entry.getTicketId(),
                    entry.getCreatedAt()
            );
        } catch (EntryDataAccessException e) {
            throw e;
        }
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
        EventDomain event = eventRepository.findById(eventId)
            .orElseThrow(() -> EventNotFoundException.EXCEPTION);
        
        List<ParticipantInfo> participants = entryQueryService.findParticipantDetailsByEventId(eventId);
        
        return participants.stream()
                .map(info -> ParticipantEntryResponse.from(info, event.getName()))
                .collect(Collectors.toList());
    }
}
