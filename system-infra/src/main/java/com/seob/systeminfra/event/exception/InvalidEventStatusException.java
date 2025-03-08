package com.seob.systeminfra.event.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systeminfra.exception.InfraException;

public class InvalidEventStatusException extends InfraException {
    public static final InfraException EXCEPTION = new InvalidEventStatusException();

    private InvalidEventStatusException() {
        super(ErrorCode.INVALID_EVENT_STATUS);
    }
}
