package com.seob.systeminfra.entry.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systeminfra.exception.InfraException;

public class EventNotOpendExcpetion extends InfraException {

    public static final InfraException EXCEPTION = new EventNotOpendExcpetion();

    private EventNotOpendExcpetion() {
        super(ErrorCode.EVENT_NOT_OPENED);
    }
}
