package com.seob.application.ticket.scheduler;

import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.repository.TicketRepository;
import com.seob.systeminfra.ticket.entity.TicketEntity;
import com.seob.systeminfra.ticket.repository.TicketJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 티켓 만료 처리를 위한 스케줄러 서비스
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TicketExpiryScheduler {

    private final TicketJpaRepository ticketJpaRepository;

    //매일 자정 티켓 만료 설정
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정
    @Transactional
    public void processExpiredTickets() {
        log.info("티켓 만료 처리 스케줄러 실행 시작");
        
        // 만료일이 지났지만 아직 만료 처리되지 않은 티켓 목록 조회
        List<TicketEntity> expiredTickets = 
            ticketJpaRepository.findByIsUsedFalseAndIsExpiredFalseAndExpiryDateLessThan(LocalDateTime.now());
        
        log.info("만료 대상 티켓 수: {}", expiredTickets.size());
        
        // 각 티켓을 만료 처리
        for (TicketEntity ticket : expiredTickets) {
            ticket.expire();
            ticketJpaRepository.save(ticket);
            log.debug("티켓 만료 처리: ID={}, 사용자={}, 만료일={}", 
                     ticket.getId(), ticket.getUserId(), ticket.getExpiryDate());
        }
        
        log.info("티켓 만료 처리 스케줄러 실행 완료. 처리된 티켓 수: {}", expiredTickets.size());
    }
}
