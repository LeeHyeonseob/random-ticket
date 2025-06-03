package com.seob.systemdomain.ticket.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;


public class TicketProcessException extends DomainException {
    public static final TicketProcessException EXCEPTION = new TicketProcessException();
    
    private TicketProcessException() {
        super(ErrorCode.TICKET_PROCESS_INTERRUPTED);
    }
}
