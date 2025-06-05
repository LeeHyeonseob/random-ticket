package com.seob.systemdomain.ticket.exception;

import com.seob.systemcore.error.ErrorCode;

public class AlreadyUsedTicketException extends TicketException {
    public static final AlreadyUsedTicketException EXCEPTION = new AlreadyUsedTicketException();

    private AlreadyUsedTicketException() {
        super(ErrorCode.TICKET_ALREADY_USED);
    }
}
