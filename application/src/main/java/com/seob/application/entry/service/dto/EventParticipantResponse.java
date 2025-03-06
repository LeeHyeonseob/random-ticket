package com.seob.application.entry.service.dto;

import com.seob.systemdomain.entry.domain.EntryDomain;
import com.seob.systemdomain.user.domain.UserDomain;

import java.time.LocalDateTime;

public record EventParticipantResponse(
        String nickname,
        String maskedEmail,
        LocalDateTime registeredAt
) {
    public static EventParticipantResponse from(UserDomain user, EntryDomain entry) {
        return new EventParticipantResponse(
                user.getNickname().getValue(),
                maskEmail(user.getEmail().getValue()),
                entry.getCreatedAt()
        );
    }

    private static String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) return email;

        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        String maskedLocalPart = localPart.charAt(0) +
                "*".repeat(Math.max(0, localPart.length() - 1));

        return maskedLocalPart + domain;
    }
}
