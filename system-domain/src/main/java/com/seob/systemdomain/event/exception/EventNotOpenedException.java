package com.seob.systemdomain.event.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;

/**
 * 이벤트가 열려 있지 않을 때 발생하는 예외
 */
public class EventNotOpenedException extends DomainException {
    public static final EventNotOpenedException EXCEPTION = new EventNotOpenedException();
    
    private EventNotOpenedException() {
        super(ErrorCode.EVENT_NOT_OPENED);
    }
}
