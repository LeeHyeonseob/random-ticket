package com.seob.systemdomain.ticket.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;


public abstract class TicketException extends DomainException {
    protected TicketException(ErrorCode errorCode) {
        super(errorCode);
    }
}
