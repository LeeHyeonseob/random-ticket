package com.seob.systemdomain.ticket.exception;

import com.seob.systemcore.error.ErrorCode;

public class TicketServiceOverloadedException extends TicketException {
    public static final TicketServiceOverloadedException EXCEPTION = new TicketServiceOverloadedException();
    
    private TicketServiceOverloadedException() {
        super(ErrorCode.TICKET_SERVICE_OVERLOADED);
    }
}
