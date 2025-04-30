package com.seob.application.entry.controller.dto;

public record EntryCreateRequest(
    String ticketId
) {
    // 티켓 ID가 제공되지 않을 수 있으므로 유효성 검증 제거
    // 컨트롤러에서 ticketId 유무에 따라 다른 로직을 타도록 처리됨

    public static EntryCreateRequest of(String ticketId) {
        return new EntryCreateRequest(ticketId);
    }
}
