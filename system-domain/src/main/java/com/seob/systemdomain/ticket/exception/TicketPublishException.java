package com.seob.systemdomain.ticket.exception;

import com.seob.systemcore.error.ErrorCode;

public class TicketPublishException extends TicketException {
    public static final TicketPublishException EXCEPTION = new TicketPublishException();
    
    private TicketPublishException() {
        super(ErrorCode.REDIS_PUBLISH_ERROR);
    }
}
