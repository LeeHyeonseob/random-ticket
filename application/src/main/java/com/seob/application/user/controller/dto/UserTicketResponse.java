package com.seob.application.user.controller.dto;

import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.dto.TicketInfo;

import java.time.LocalDateTime;

public record UserTicketResponse(
    String id,
    boolean isUsed,
    boolean isExpired,
    LocalDateTime createdAt,
    Long eventId
) {
    public static UserTicketResponse of(TicketDomain domain) {
        return new UserTicketResponse(
            domain.getId().getValue(),
            domain.isUsed(),
            domain.getIsExpired(),
            domain.getCreatedAt(),
            domain.getEventId()
        );
    }
    
    // 클라이언트 표시용 상태 텍스트
    public String getStatusText() {
        if (isUsed) return "사용완료";
        if (isExpired) return "만료됨";
        return "사용가능";
    }
}
