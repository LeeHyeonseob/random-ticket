package com.seob.application.entry.service;

import com.seob.application.entry.service.dto.EventEntryResponse;
import com.seob.application.entry.service.dto.EventParticipantResponse;
import com.seob.systemdomain.entry.dto.ParticipantInfo;
import com.seob.systemdomain.entry.repository.EntryRepository;
import com.seob.systemdomain.entry.service.EntryService;
import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventEntryFacadeService {

    private final EntryService entryService;
    private final EventRepository eventRepository;
    private final EntryRepository entryRepository;

    @Transactional(readOnly = true)
    public EventEntryResponse getEventWithParticipants(Long eventId){

        //이벤트 찾기
        EventDomain event = eventRepository.findById(eventId);

        //참여자 아이디 내역
        List<ParticipantInfo> participantDetails = entryRepository.findParticipantDetailsByEventId(eventId);

        List<EventParticipantResponse> participants = participantDetails.stream()
                .map(detail -> new EventParticipantResponse(
                        detail.nickname(),
                        maskEmail(detail.email()),
                        detail.registerTime()
                ))
                .toList();

        return EventEntryResponse.from(event, participants);



    }


    private String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) return email;

        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        String maskedLocalPart = localPart.charAt(0) +
                "*".repeat(Math.max(0, localPart.length() - 1));

        return maskedLocalPart + domain;
    }

}
