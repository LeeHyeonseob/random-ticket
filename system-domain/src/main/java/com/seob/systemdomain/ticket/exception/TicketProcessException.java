package com.seob.systemdomain.ticket.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;

/**
 * 티켓 처리 중 발생한 예외
 */
public class TicketProcessException extends DomainException {
    public static final TicketProcessException EXCEPTION = new TicketProcessException();
    
    private TicketProcessException() {
        super(ErrorCode.TICKET_PROCESS_INTERRUPTED);
    }
}
