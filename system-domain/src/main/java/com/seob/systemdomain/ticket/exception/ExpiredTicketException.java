package com.seob.systemdomain.ticket.exception;

import com.seob.systemcore.error.ErrorCode;

public class ExpiredTicketException extends TicketException {
    public static final ExpiredTicketException EXCEPTION = new ExpiredTicketException();
    
    private ExpiredTicketException() {
        super(ErrorCode.EXPIRED_TICKET);
    }
}
