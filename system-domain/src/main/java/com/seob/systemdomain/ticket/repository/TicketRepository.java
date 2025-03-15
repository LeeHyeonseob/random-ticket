package com.seob.systemdomain.ticket.repository;

import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.domain.vo.TicketId;
import com.seob.systemdomain.user.domain.vo.UserId;

import java.util.Optional;

public interface TicketRepository {

    TicketDomain save(TicketDomain ticketDomain);

    Optional<TicketDomain> findById(TicketId id);

    Optional<TicketDomain> findByUserId(UserId userId);

    boolean existsByUserId(UserId userId); //이미 있는지 확인용
}
