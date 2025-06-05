package com.seob.systeminfra.event.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systeminfra.exception.InfraException;


public class EventDataAccessException extends InfraException {
    public static final EventDataAccessException EXCEPTION = new EventDataAccessException(ErrorCode.INTERNAL_SERVER_ERROR);
    
    private EventDataAccessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
