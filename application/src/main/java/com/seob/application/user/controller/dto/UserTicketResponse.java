package com.seob.application.user.controller.dto;

import com.seob.systemdomain.ticket.dto.TicketInfo;

import java.time.LocalDateTime;

public record UserTicketResponse(
    String id,
    boolean isUsed,
    boolean isExpired,
    LocalDateTime createdAt
) {
    public static UserTicketResponse of(TicketInfo info) {
        return new UserTicketResponse(
            info.id(),
            info.isUsed(),
            info.isExpired(),
            info.createdAt()
        );
    }
    
    // 클라이언트 표시용 상태 텍스트
    public String getStatusText() {
        if (isUsed) return "사용완료";
        if (isExpired) return "만료됨";
        return "사용가능";
    }
}
