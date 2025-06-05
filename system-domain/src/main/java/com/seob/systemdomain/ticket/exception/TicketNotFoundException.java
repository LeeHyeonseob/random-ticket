package com.seob.systemdomain.ticket.exception;

import com.seob.systemcore.error.ErrorCode;

public class TicketNotFoundException extends TicketException {
    public static final TicketNotFoundException EXCEPTION = new TicketNotFoundException();

    private TicketNotFoundException() {
        super(ErrorCode.TICKET_NOT_FOUND);
    }
}
