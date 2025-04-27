package com.seob.systemdomain.ticket.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;

/**
 * 중복 티켓 발급 시도 예외
 */
public class DuplicateTicketIssuanceException extends DomainException {
    public static final DuplicateTicketIssuanceException EXCEPTION = 
            new DuplicateTicketIssuanceException();
    
    private DuplicateTicketIssuanceException() {
        super(ErrorCode.DUPLICATED_TICKET);
    }
}
