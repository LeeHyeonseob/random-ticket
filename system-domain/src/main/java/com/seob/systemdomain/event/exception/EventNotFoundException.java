package com.seob.systemdomain.event.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;

/**
 * 이벤트를 찾을 수 없을 때 발생하는 예외
 */
public class EventNotFoundException extends DomainException {
    public static final EventNotFoundException EXCEPTION = new EventNotFoundException();
    
    private EventNotFoundException() {
        super(ErrorCode.EVENT_NOT_FOUND);
    }
}
