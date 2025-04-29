package com.seob.application.entry.controller.dto;

public record EntryCreateRequest(
    Long eventId,
    String ticketId
) {
    public EntryCreateRequest {
        if (eventId == null) {
            throw new IllegalArgumentException("이벤트 ID는 필수 입력값입니다.");
        }
        if (ticketId == null || ticketId.isBlank()) {
            throw new IllegalArgumentException("티켓 ID는 필수 입력값입니다.");
        }
    }

    public static EntryCreateRequest of(Long eventId, String ticketId) {
        return new EntryCreateRequest(eventId, ticketId);
    }
}
