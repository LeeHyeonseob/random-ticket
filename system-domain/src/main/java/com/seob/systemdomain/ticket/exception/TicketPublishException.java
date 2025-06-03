package com.seob.systemdomain.ticket.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;


public class TicketPublishException extends DomainException {
    public static final TicketPublishException EXCEPTION = new TicketPublishException();
    
    private TicketPublishException() {
        super(ErrorCode.REDIS_PUBLISH_ERROR);
    }
}
