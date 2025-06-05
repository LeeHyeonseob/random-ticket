package com.seob.systemdomain.ticket.exception;

import com.seob.systemcore.error.ErrorCode;

public class TicketProcessException extends TicketException {
    public static final TicketProcessException EXCEPTION = new TicketProcessException();
    
    private TicketProcessException() {
        super(ErrorCode.TICKET_PROCESS_INTERRUPTED);
    }
}
