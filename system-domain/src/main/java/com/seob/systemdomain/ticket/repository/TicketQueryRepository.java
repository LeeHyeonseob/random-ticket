package com.seob.systemdomain.ticket.repository;

import com.seob.systemdomain.ticket.dto.TicketInfo;

import java.util.List;

/**
 * 티켓 조회 레포지토리
 */
public interface TicketQueryRepository {

    /**
     * 사용자 ID로 티켓 목록 조회
     * @param userId 사용자 ID
     * @return 티켓 정보 목록
     */
    List<TicketInfo> findByUserId(String userId);
    
    /**
     * 티켓 ID로 티켓 상세 정보 조회
     * @param ticketId 티켓 ID
     * @return 티켓 정보
     */
    TicketInfo findById(String ticketId);
}
