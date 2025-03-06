package com.seob.application.entry.service.dto;

import com.seob.systemdomain.event.domain.EventDomain;

import java.util.List;

public record EventEntryResponse(
        Long eventId,
        String eventName,
        int participantCount,
        List<EventParticipantResponse> participants
) {
    public static EventEntryResponse from(EventDomain event, List<EventParticipantResponse> participants) {
        return new EventEntryResponse(
                event.getId(),
                event.getName(),
                participants.size(),
                participants
        );
    }
}
