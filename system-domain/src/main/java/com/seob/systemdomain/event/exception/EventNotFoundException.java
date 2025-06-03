package com.seob.systemdomain.event.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;


public class EventNotFoundException extends DomainException {
    public static final EventNotFoundException EXCEPTION = new EventNotFoundException();
    
    private EventNotFoundException() {
        super(ErrorCode.EVENT_NOT_FOUND);
    }
}
