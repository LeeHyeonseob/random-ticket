package com.seob.systemdomain.ticket.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;


public class DuplicateTicketIssuanceException extends DomainException {
    public static final DuplicateTicketIssuanceException EXCEPTION = 
            new DuplicateTicketIssuanceException();
    
    private DuplicateTicketIssuanceException() {
        super(ErrorCode.DUPLICATED_TICKET);
    }
}
