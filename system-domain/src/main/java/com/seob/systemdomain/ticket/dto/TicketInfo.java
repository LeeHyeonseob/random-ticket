package com.seob.systemdomain.ticket.dto;

import java.time.LocalDateTime;

//티켓 정보
public record TicketInfo(
    String id,
    String userId,
    boolean isUsed,
    boolean isExpired,
    LocalDateTime createdAt
) {
    // 티켓 유효성 확인
    public boolean isValid() {
        return !isUsed && !isExpired;
    }
}
