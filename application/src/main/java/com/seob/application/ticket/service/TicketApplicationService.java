package com.seob.application.ticket.service;

import com.seob.application.auth.CustomUserDetails;
import com.seob.application.ticket.controller.dto.TicketResponseDto;
import com.seob.systemdomain.event.service.EventValidationService;
import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.service.TicketService;
import com.seob.systemdomain.user.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketApplicationService {
    private final TicketService ticketService;
    private final EventValidationService eventValidationService;
    
    // 티켓 발급
    @Transactional
    public TicketResponseDto issueTicket(Long eventId, CustomUserDetails userDetails) {
        UserId userId = userDetails.getUserId();
        log.info("사용자 티켓 발급 처리 시작 - 사용자: {}, 이벤트: {}", userId.getValue(), eventId);
        
        // 이벤트 존재 여부 확인
        eventValidationService.validateEventExists(eventId);
        
        // 티켓 발급
        TicketDomain ticket = ticketService.issueTicket(userId, eventId);
        log.info("티켓 발급 성공 - 티켓 ID: {}, 이벤트: {}", ticket.getId().getValue(), eventId);
        
        return TicketResponseDto.of(ticket);
    }
}