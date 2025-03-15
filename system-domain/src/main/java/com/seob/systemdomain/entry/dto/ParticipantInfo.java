package com.seob.systemdomain.entry.dto;


import java.time.LocalDateTime;

public record ParticipantInfo(
        String nickname,
        String email,
        LocalDateTime registerTime
) {
    public static ParticipantInfo of(String nickname, String email, LocalDateTime registerTime) {
        return new ParticipantInfo(nickname, email, registerTime);
    }

}