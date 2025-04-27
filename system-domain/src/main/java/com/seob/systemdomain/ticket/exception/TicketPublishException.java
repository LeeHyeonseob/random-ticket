package com.seob.systemdomain.ticket.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;

/**
 * 티켓 이벤트 발행 실패 예외
 */
public class TicketPublishException extends DomainException {
    public static final TicketPublishException EXCEPTION = new TicketPublishException();
    
    private TicketPublishException() {
        super(ErrorCode.REDIS_PUBLISH_ERROR);
    }
}
