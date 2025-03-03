package com.seob.systeminfra.event.exception;

import com.seob.systeminfra.exception.InfraException;
import com.seob.systemcore.error.ErrorCode;

public class EventNotFoundException extends InfraException {
    public static final InfraException EXCEPTION = new EventNotFoundException();

    private EventNotFoundException(){
        super(ErrorCode.EVENT_NOT_FOUND);
    }
}
