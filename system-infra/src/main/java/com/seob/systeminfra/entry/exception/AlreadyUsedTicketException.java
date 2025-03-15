package com.seob.systeminfra.entry.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systeminfra.exception.InfraException;


public class AlreadyUsedTicketException extends InfraException {
    public static final InfraException EXCEPTION = new AlreadyUsedTicketException();

    private AlreadyUsedTicketException() {
        super(ErrorCode.TICKET_ALREADY_USED);
    }
}
