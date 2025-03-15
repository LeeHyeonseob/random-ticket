package com.seob.application.entry.service.dto;

import com.seob.systemdomain.entry.domain.EntryDomain;
import com.seob.systemdomain.user.domain.UserDomain;

import java.time.LocalDateTime;

public record EventParticipantResponse(
        String nickname,
        String maskedEmail,
        LocalDateTime registeredAt
) {
    public static EventParticipantResponse of(String nickname, String maskedEmail, LocalDateTime registeredAt) {
        return new EventParticipantResponse(nickname, maskedEmail, registeredAt);
    }



}
