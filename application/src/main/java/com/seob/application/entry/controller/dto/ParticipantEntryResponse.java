package com.seob.application.entry.controller.dto;

import com.seob.systemdomain.entry.dto.ParticipantInfo;

import java.time.LocalDateTime;

public record ParticipantEntryResponse(
    String nickname,
    String email,
    LocalDateTime registeredAt,
    String eventName
) {
    public static ParticipantEntryResponse from(ParticipantInfo info, String eventName) {
        return new ParticipantEntryResponse(
            info.nickname(),
            info.email(),
            info.registerTime(),
            eventName
        );
    }
}
