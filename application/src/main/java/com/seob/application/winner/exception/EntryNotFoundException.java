package com.seob.application.winner.exception;

import com.seob.application.exception.ServiceException;
import com.seob.systemcore.error.ErrorCode;

public class EntryNotFoundException extends ServiceException {
    public static ServiceException EXCEPTION = new EntryNotFoundException();

    private EntryNotFoundException() {
        super(ErrorCode.ENTRY_NOT_FOUND);
    }
}
