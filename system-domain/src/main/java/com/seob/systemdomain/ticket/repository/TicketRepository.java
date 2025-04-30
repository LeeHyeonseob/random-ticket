package com.seob.systemdomain.ticket.repository;

import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.domain.vo.TicketId;
import com.seob.systemdomain.user.domain.vo.UserId;

import java.util.Optional;

public interface TicketRepository {

    TicketDomain save(TicketDomain ticketDomain);

    Optional<TicketDomain> findById(TicketId id);

    Optional<TicketDomain> findByUserId(UserId userId);
    
    // 특정 이벤트에 대한 사용자 티켓 찾기
    Optional<TicketDomain> findByUserIdAndEventIdAndNotUsed(UserId userId, Long eventId);
    
    // 사용자 미사용 티켓 찾기
    Optional<TicketDomain> findByUserIdAndNotUsed(UserId userId);

    boolean existsByUserId(UserId userId); //이미 있는지 확인용
}
