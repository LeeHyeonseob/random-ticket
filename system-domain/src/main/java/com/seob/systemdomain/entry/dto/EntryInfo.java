package com.seob.systemdomain.entry.dto;

import java.time.LocalDateTime;

/**
 * 이벤트 참여 정보 DTO
 * 참여 내역 조회를 위한 정보를 담고 있습니다.
 */
public record EntryInfo(
    Long id,
    Long eventId,
    String eventName,
    String ticketId,
    LocalDateTime createdAt
) {
    /**
     * EntryInfo 생성 팩토리 메소드
     */
    public static EntryInfo of(
        Long id,
        Long eventId,
        String eventName,
        String ticketId,
        LocalDateTime createdAt
    ) {
        return new EntryInfo(id, eventId, eventName, ticketId, createdAt);
    }
}
