package com.seob.systemdomain.ticket.service;

import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.user.domain.vo.UserId;

public interface TicketService {
    /**
     * 티켓 발급
     * @param userId 사용자 ID
     * @param eventId 이벤트 ID
     * @return 발급된 티켓
     */
    TicketDomain issueTicket(UserId userId, Long eventId);
}
