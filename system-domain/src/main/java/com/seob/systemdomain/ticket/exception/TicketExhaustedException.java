package com.seob.systemdomain.ticket.exception;

import com.seob.systemcore.error.ErrorCode;

public class TicketExhaustedException extends TicketException {
    public static final TicketExhaustedException EXCEPTION = new TicketExhaustedException();
    
    private TicketExhaustedException() {
        super(ErrorCode.TICKET_ISSUANCE_EXHAUSTED);
    }
}
