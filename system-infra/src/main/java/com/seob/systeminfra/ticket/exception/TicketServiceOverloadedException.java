package com.seob.systeminfra.ticket.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systeminfra.exception.InfraException;

public class TicketServiceOverloadedException extends InfraException {
    public static final InfraException EXCEPTION = new TicketServiceOverloadedException();

    private TicketServiceOverloadedException() {
        super(ErrorCode.TICKET_SERVICE_OVERLOADED);
    }
}
