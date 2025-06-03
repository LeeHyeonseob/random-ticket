package com.seob.systemdomain.event.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;


public class EventNotOpenedException extends DomainException {
    public static final EventNotOpenedException EXCEPTION = new EventNotOpenedException();
    
    private EventNotOpenedException() {
        super(ErrorCode.EVENT_NOT_OPENED);
    }
}
