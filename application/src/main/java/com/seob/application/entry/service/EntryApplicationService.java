package com.seob.application.entry.service;

import com.seob.application.entry.service.dto.EventEntryResponse;
import com.seob.application.entry.service.dto.EventParticipantResponse;
import com.seob.application.entry.service.dto.UserEntryResponse;
import com.seob.systemcore.error.utils.PrivacyUtils;
import com.seob.systemdomain.entry.dto.ParticipantInfo;
import com.seob.systemdomain.entry.dto.UserEventInfo;
import com.seob.systemdomain.entry.service.EntryService;
import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EntryApplicationService {

    private final EventService eventService;
    private final EntryService entryService;

    @Transactional(readOnly = true)
    public EventEntryResponse getEventWithParticipants(Long eventId) {
        // 도메인 서비스를 통해 데이터 조회
        EventDomain event = eventService.findById(eventId);

        // EntryService 인터페이스에 메서드 추가 필요
        List<ParticipantInfo> participantDetails = entryService.findParticipantDetailsByEventId(eventId);

        List<EventParticipantResponse> participants = participantDetails.stream()
                .map(detail -> EventParticipantResponse.of(
                        detail.nickname(),
                        PrivacyUtils.maskEmail(detail.email()),
                        detail.registerTime()
                ))
                .toList();

        return EventEntryResponse.from(event, participants);
    }

    @Transactional(readOnly = true)
    public List<UserEntryResponse> getUserEntries(String userId) {
        // EntryService 인터페이스에 메서드 추가 필요
        List<UserEventInfo> userEventInfos = entryService.findUserEventInfoByUserId(userId);

        return userEventInfos.stream()
                .map(info -> UserEntryResponse.of(
                        info.eventName(),
                        info.eventDate(),
                        info.registeredAt()
                ))
                .toList();
    }
}
