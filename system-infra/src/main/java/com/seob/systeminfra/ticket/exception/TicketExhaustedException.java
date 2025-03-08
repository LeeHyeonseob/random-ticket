package com.seob.systeminfra.ticket.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systeminfra.exception.InfraException;


public class TicketExhaustedException extends InfraException {
    public static final InfraException EXCEPTION = new TicketExhaustedException();

    private TicketExhaustedException() {
        super(ErrorCode.TICKET_ISSUANCE_EXHAUSTED);
    }
}
