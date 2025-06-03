package com.seob.systemdomain.ticket.repository;

import com.seob.systemdomain.user.domain.vo.UserId;


public interface TicketIssuanceRepository {

    boolean hasIssuedTicket(UserId userId);

    void saveIssuance(UserId userId);

    void cancelIssuance(UserId userId);

    long incrementTicketCount();

    void decrementTicketCount();

    int getMaxTickets();
}
