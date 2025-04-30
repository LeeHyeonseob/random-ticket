package com.seob.systemdomain.ticket.dto;

import java.time.LocalDateTime;

//티켓 정보
public record TicketInfo(
    String id,
    String userId,
    Long eventId,
    boolean isUsed,
    boolean isExpired,
    LocalDateTime createdAt,
    LocalDateTime expiryDate
) {
    // 티켓 유효성 확인
    public boolean isValid() {
        return !isUsed && !isExpired;
    }
    
    // 간단한 생성자 오버로드 (이전 버전과의 호환성)
    public TicketInfo(String id, String userId, boolean isUsed, boolean isExpired, LocalDateTime createdAt) {
        this(id, userId, null, isUsed, isExpired, createdAt, null);
    }
}
