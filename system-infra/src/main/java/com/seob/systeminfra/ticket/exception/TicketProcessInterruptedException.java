package com.seob.systeminfra.ticket.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systeminfra.exception.InfraException;

public class TicketProcessInterruptedException extends InfraException {
    public static final InfraException EXCEPTION = new TicketProcessInterruptedException();

    private TicketProcessInterruptedException() {
        super(ErrorCode.TICKET_PROCESS_INTERRUPTED);
    }
}
