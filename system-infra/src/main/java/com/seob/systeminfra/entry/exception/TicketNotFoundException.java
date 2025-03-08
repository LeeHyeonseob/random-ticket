package com.seob.systeminfra.entry.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systeminfra.exception.InfraException;

public class TicketNotFoundException extends InfraException {
    public static final InfraException EXCEPTION = new TicketNotFoundException();

    private TicketNotFoundException() {
        super(ErrorCode.TICKET_NOT_FOUND);
    }
}
