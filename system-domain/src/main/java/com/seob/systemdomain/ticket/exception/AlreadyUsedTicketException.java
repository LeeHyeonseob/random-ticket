package com.seob.systemdomain.ticket.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;

public class AlreadyUsedTicketException extends DomainException {

    public static final DomainException EXCEPTION = new AlreadyUsedTicketException();
    private AlreadyUsedTicketException() {
        super(ErrorCode.TICKET_ALREADY_USED);
    }
}
