package com.seob.application.ticket.service;

import com.seob.application.auth.CustomUserDetails;
import com.seob.application.exception.ServiceException;
import com.seob.application.ticket.controller.dto.TicketResponseDto;
import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.event.service.EventValidationService;
import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.exception.DuplicateTicketIssuanceException;
import com.seob.systemdomain.ticket.exception.EventNotFoundException;
import com.seob.systemdomain.ticket.exception.TicketExhaustedException;
import com.seob.systemdomain.ticket.exception.TicketProcessException;
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
        log.info("Processing ticket issuance for user: {} and event: {}", userId.getValue(), eventId);
        
        try {
            // 이벤트 존재 여부 확인
            eventValidationService.validateEventExists(eventId);
            
            // 티켓 발급
            TicketDomain ticket = ticketService.issueTicket(userId, eventId);
            log.info("Ticket issued successfully: {} for event: {}", ticket.getId().getValue(), eventId);
            
            return TicketResponseDto.of(ticket);
        } catch (EventNotFoundException e) {
            log.warn("Event not found: {}", eventId);
            throw new ServiceException(ErrorCode.EVENT_NOT_FOUND);
        } catch (DuplicateTicketIssuanceException e) {
            log.warn("Duplicate ticket issuance attempt for user: {} and event: {}", userId.getValue(), eventId);
            throw new ServiceException(ErrorCode.DUPLICATED_TICKET);
        } catch (TicketExhaustedException e) {
            log.warn("No more tickets available for event: {}", eventId);
            throw new ServiceException(ErrorCode.TICKET_ISSUANCE_EXHAUSTED);
        } catch (TicketProcessException e) {
            log.error("Error processing ticket for event: {}", eventId, e);
            throw new ServiceException(ErrorCode.TICKET_PROCESS_INTERRUPTED);
        } catch (Exception e) {
            log.error("Unexpected error during ticket issuance for event: {}", eventId, e);
            throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}