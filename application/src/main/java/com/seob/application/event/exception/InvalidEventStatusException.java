package com.seob.application.event.exception;

import com.seob.application.exception.ServiceException;
import com.seob.systemcore.error.ErrorCode;

public class InvalidEventStatusException extends ServiceException {
    public static final ServiceException EXCEPTION = new InvalidEventStatusException();

    private InvalidEventStatusException() {
        super(ErrorCode.INVALID_EVENT_STATUS);
    }
}
