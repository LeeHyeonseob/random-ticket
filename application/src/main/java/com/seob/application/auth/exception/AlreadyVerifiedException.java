package com.seob.application.auth.exception;

import com.seob.application.exception.ServiceException;
import com.seob.systemcore.error.ErrorCode;

public class AlreadyVerifiedException extends ServiceException {
    public static final ServiceException EXCEPTION = new AlreadyVerifiedException();

    private AlreadyVerifiedException() {
        super(ErrorCode.ALREADY_VERIFY_CODE);
    }
}
