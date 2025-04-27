package com.seob.application.ticket.service;

import com.seob.application.auth.CustomUserDetails;
import com.seob.application.exception.ServiceException;
import com.seob.application.ticket.controller.dto.TicketResponseDto;
import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.exception.DuplicateTicketIssuanceException;
import com.seob.systemdomain.ticket.exception.TicketExhaustedException;
import com.seob.systemdomain.ticket.exception.TicketProcessException;
import com.seob.systemdomain.ticket.service.TicketService;
import com.seob.systemdomain.user.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 티켓 애플리케이션 서비스
 * 컨트롤러와 도메인 계층 사이의 조정 역할
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TicketApplicationService {
    private final TicketService ticketService;
    
    /**
     * 티켓 발급 처리
     */
    @Transactional
    public TicketResponseDto issueTicket(CustomUserDetails userDetails) {
        UserId userId = userDetails.getUserId();
        log.info("Processing ticket issuance for user: {}", userId.getValue());
        
        try {
            TicketDomain ticket = ticketService.issueTicket(userId);
            log.info("Ticket issued successfully: {}", ticket.getId().getValue());
            
            return TicketResponseDto.of(ticket);
        } catch (DuplicateTicketIssuanceException e) {
            log.warn("Duplicate ticket issuance attempt for user: {}", userId.getValue());
            throw new ServiceException(ErrorCode.DUPLICATED_TICKET);
        } catch (TicketExhaustedException e) {
            log.warn("No more tickets available");
            throw new ServiceException(ErrorCode.TICKET_ISSUANCE_EXHAUSTED);
        } catch (TicketProcessException e) {
            log.error("Error processing ticket", e);
            throw new ServiceException(ErrorCode.TICKET_PROCESS_INTERRUPTED);
        } catch (Exception e) {
            log.error("Unexpected error during ticket issuance", e);
            throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}