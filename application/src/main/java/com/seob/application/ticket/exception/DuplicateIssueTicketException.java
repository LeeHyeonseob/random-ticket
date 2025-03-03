package com.seob.application.ticket.exception;

import com.seob.application.exception.ServiceException;
import com.seob.systemcore.error.ErrorCode;

public class DuplicateIssueTicketException extends ServiceException {
    public static final ServiceException EXCEPTION = new DuplicateIssueTicketException();

    private DuplicateIssueTicketException() {
        super(ErrorCode.DUPLICATED_TICKET);
    }
}
