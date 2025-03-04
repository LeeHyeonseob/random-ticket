package com.seob.application.ticket.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.application.exception.ServiceException;


public class TicketExhaustedException extends ServiceException {
    public static final ServiceException EXCEPTION = new TicketExhaustedException();

    private TicketExhaustedException() {
        super(ErrorCode.TICKET_ISSUANCE_EXHAUSTED);
    }
}
