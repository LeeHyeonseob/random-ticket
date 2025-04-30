package com.seob.systeminfra.ticket.repository;

import com.seob.systeminfra.ticket.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketJpaRepository extends JpaRepository<TicketEntity, String> {
    Optional<TicketEntity> findByUserId(String userId);
    List<TicketEntity> findAllByUserId(String userId);
    List<TicketEntity> findByUserIdOrderByCreatedAtDesc(String userId);
    boolean existsByUserId(String userId);
    
    // 사용자 ID와 이벤트 ID로 미사용/미만료 티켓 찾기
    Optional<TicketEntity> findByUserIdAndEventIdAndIsUsedFalseAndIsExpiredFalse(String userId, Long eventId);
    
    // 사용자 ID로 미사용/미만료 티켓 찾기
    Optional<TicketEntity> findByUserIdAndIsUsedFalseAndIsExpiredFalse(String userId);
    
    // 만료일이 지난 미사용 티켓 찾기 - 스케줄러용
    List<TicketEntity> findByIsUsedFalseAndIsExpiredFalseAndExpiryDateLessThan(java.time.LocalDateTime now);
}
