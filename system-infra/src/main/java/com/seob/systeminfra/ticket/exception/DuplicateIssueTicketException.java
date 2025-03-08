package com.seob.systeminfra.ticket.exception;


import com.seob.systemcore.error.ErrorCode;
import com.seob.systeminfra.exception.InfraException;

public class DuplicateIssueTicketException extends InfraException {
    public static final InfraException EXCEPTION = new DuplicateIssueTicketException();

    private DuplicateIssueTicketException() {
        super(ErrorCode.DUPLICATED_TICKET);
    }
}
