package com.seob.systemdomain.ticket.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;


public class TicketServiceOverloadedException extends DomainException {
    public static final TicketServiceOverloadedException EXCEPTION = new TicketServiceOverloadedException();
    
    private TicketServiceOverloadedException() {
        super(ErrorCode.TICKET_SERVICE_OVERLOADED);
    }
}
