package com.seob.application.auth.exception;

import com.seob.application.exception.ServiceException;
import com.seob.systemcore.error.ErrorCode;

public class InvalidCodeException extends ServiceException {
    public static final ServiceException EXCEPTION = new InvalidCodeException();

    private InvalidCodeException() {
        super(ErrorCode.INVALID_CODE);
    }
}
