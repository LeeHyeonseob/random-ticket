package com.seob.systemdomain.ticket.repository;

import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.domain.vo.TicketId;
import com.seob.systemdomain.user.domain.vo.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
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
    
    // 필터링이 적용된 사용자 티켓 조회
    Page<TicketDomain> findByUserIdWithFilters(String userId, Boolean used, Boolean expired, Pageable pageable);
    
    // 만료일이 지난 미사용 티켓 찾기 - 스케줄러용
    List<TicketDomain> findByIsUsedFalseAndIsExpiredFalseAndExpiryDateLessThan(LocalDateTime now);
}
