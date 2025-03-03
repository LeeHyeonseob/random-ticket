package com.seob.systemdomain.ticket.service;

import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.user.domain.vo.UserId;

public interface TicketService {
    TicketDomain issueTicket(UserId userId);
}
