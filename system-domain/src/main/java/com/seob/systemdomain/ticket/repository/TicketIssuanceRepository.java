package com.seob.systemdomain.ticket.repository;

import com.seob.systemdomain.user.domain.vo.UserId;

/**
 * 티켓 발급 처리를 위한 저장소 인터페이스
 * 실제 구현은 인프라 계층에서 담당
 */
public interface TicketIssuanceRepository {

    boolean hasIssuedTicket(UserId userId);

    void saveIssuance(UserId userId);

    void cancelIssuance(UserId userId);

    long incrementTicketCount();

    void decrementTicketCount();

    int getMaxTickets();
}
