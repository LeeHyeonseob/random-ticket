package com.seob.systeminfra.ticket.repository;

import com.seob.systemdomain.ticket.dto.TicketInfo;
import com.seob.systemdomain.ticket.repository.TicketQueryRepository;
import com.seob.systeminfra.ticket.entity.TicketEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TicketQueryRepository 구현체
 * 티켓 조회 관련 쿼리를 처리합니다.
 */
@Repository
@RequiredArgsConstructor
public class TicketQueryRepositoryImpl implements TicketQueryRepository {

    private final TicketJpaRepository ticketJpaRepository;

    @Override
    public List<TicketInfo> findByUserId(String userId) {
        // 기본 쿼리를 사용해 결과를 가져옴
        List<TicketEntity> tickets =
            ticketJpaRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        // 결과를 TicketInfo 객체로 변환
        List<TicketInfo> ticketInfos = new ArrayList<>();
        
        for (TicketEntity ticket : tickets) {
            // 티켓 유효성 검사
            boolean isExpired = ticket.isExpired() || 
                              (ticket.getExpiryDate() != null && 
                               LocalDateTime.now().isAfter(ticket.getExpiryDate()));
            
            // TicketInfo 객체 생성
            ticketInfos.add(new TicketInfo(
                ticket.getId(),
                ticket.getUserId(),
                ticket.getEventId(),
                ticket.isUsed(),
                isExpired,
                ticket.getCreatedAt(),
                ticket.getExpiryDate()
            ));
        }
        
        return ticketInfos;
    }

    @Override
    public TicketInfo findById(String ticketId) {
        // ticketId로 티켓을 찾음
        TicketEntity ticket =
            ticketJpaRepository.findById(ticketId).orElse(null);
        
        if (ticket == null) {
            return null;
        }
        
        // 티켓 유효성 검사
        boolean isExpired = ticket.isExpired() || 
                          (ticket.getExpiryDate() != null && 
                           LocalDateTime.now().isAfter(ticket.getExpiryDate()));
        
        // TicketInfo 객체 생성
        return new TicketInfo(
            ticket.getId(),
            ticket.getUserId(),
            ticket.getEventId(),
            ticket.isUsed(),
            isExpired,
            ticket.getCreatedAt(),
            ticket.getExpiryDate()
        );
    }
}
