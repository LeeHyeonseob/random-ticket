package com.seob.systemdomain.ticket.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;

/**
 * 티켓 소진 예외
 */
public class TicketExhaustedException extends DomainException {
    public static final TicketExhaustedException EXCEPTION = new TicketExhaustedException();
    
    private TicketExhaustedException() {
        super(ErrorCode.TICKET_ISSUANCE_EXHAUSTED);
    }
}
