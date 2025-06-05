package com.seob.systemdomain.ticket.exception;

import com.seob.systemcore.error.ErrorCode;

public class DuplicateTicketIssuanceException extends TicketException {
    public static final DuplicateTicketIssuanceException EXCEPTION = 
            new DuplicateTicketIssuanceException();
    
    private DuplicateTicketIssuanceException() {
        super(ErrorCode.DUPLICATED_TICKET);
    }
}
