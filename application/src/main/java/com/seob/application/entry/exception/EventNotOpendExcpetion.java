package com.seob.application.entry.exception;

import com.seob.application.exception.ServiceException;
import com.seob.systemcore.error.ErrorCode;

public class EventNotOpendExcpetion extends ServiceException {

    public static  final ServiceException EXCEPTION = new EventNotOpendExcpetion();

    private EventNotOpendExcpetion() {
        super(ErrorCode.EVENT_NOT_OPENED);
    }
}
