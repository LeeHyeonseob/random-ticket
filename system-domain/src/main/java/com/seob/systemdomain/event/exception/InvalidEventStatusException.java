package com.seob.systemdomain.event.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;

/**
 * 잘못된 이벤트 상태일 때 발생하는 예외
 */
public class InvalidEventStatusException extends DomainException {
    public static final InvalidEventStatusException EXCEPTION = new InvalidEventStatusException();
    
    private InvalidEventStatusException() {
        super(ErrorCode.INVALID_EVENT_STATUS);
    }
}
