package com.seob.application.entry.exception;

import com.seob.application.exception.ServiceException;
import com.seob.systemcore.error.ErrorCode;


public class AlreadyUsedTicketException extends ServiceException {
    public static final ServiceException EXCEPTION = new AlreadyUsedTicketException();

    private AlreadyUsedTicketException() {
        super(ErrorCode.TICKET_ALREADY_USED);
    }
}
