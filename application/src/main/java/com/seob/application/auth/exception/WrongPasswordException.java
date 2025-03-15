package com.seob.application.auth.exception;

import com.seob.application.exception.ServiceException;
import com.seob.systemcore.error.ErrorCode;

public class WrongPasswordException extends ServiceException {
    public static final ServiceException EXCEPTION = new WrongPasswordException();

    private WrongPasswordException() {
        super(ErrorCode.PASSWORD_MISMATCH);
    }
}
