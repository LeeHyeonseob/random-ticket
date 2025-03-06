package com.seob.application.ticket.exception;

import com.seob.application.exception.ServiceException;
import com.seob.systemcore.error.ErrorCode;

public class TicketNotFoundException extends ServiceException {
    public static final ServiceException EXCEPTION = new TicketNotFoundException();

    private TicketNotFoundException() {
        super(ErrorCode.TICKET_NOT_FOUND);
    }
}
