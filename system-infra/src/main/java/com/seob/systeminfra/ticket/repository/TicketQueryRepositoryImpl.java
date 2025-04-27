package com.seob.systeminfra.ticket.repository;

import com.seob.systemdomain.ticket.dto.TicketInfo;
import com.seob.systemdomain.ticket.repository.TicketQueryRepository;
import com.seob.systeminfra.ticket.entity.TicketEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
            // TicketInfo 객체 생성 (isExpired는 false로 설정 - 스케줄러가 관리)
            ticketInfos.add(new TicketInfo(
                ticket.getId(),
                ticket.getUserId(),
                ticket.isUsed(),
                false, // isExpired (스케줄러에서 관리하므로 항상 false)
                ticket.getCreatedAt()
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
        
        // TicketInfo 객체 생성
        return new TicketInfo(
            ticket.getId(),
            ticket.getUserId(),
            ticket.isUsed(),
            false, // isExpired (스케줄러에서 관리하므로 항상 false)
            ticket.getCreatedAt()
        );
    }
}
