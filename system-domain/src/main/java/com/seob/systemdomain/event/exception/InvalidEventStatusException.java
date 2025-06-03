package com.seob.systemdomain.event.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;


public class InvalidEventStatusException extends DomainException {
    public static final InvalidEventStatusException EXCEPTION = new InvalidEventStatusException();
    
    private InvalidEventStatusException() {
        super(ErrorCode.INVALID_EVENT_STATUS);
    }
}
