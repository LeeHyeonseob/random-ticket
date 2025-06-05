package com.seob.systeminfra.exception;

import com.seob.systemcore.error.ErrorCode;

public class TicketNotFoundException extends InfraException {
    public static final InfraException EXCEPTION = new TicketNotFoundException();

    private TicketNotFoundException() {
        super(ErrorCode.TICKET_NOT_FOUND);
    }
}
