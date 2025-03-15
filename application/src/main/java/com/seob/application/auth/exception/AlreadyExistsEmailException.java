package com.seob.application.auth.exception;

import com.seob.application.exception.ServiceException;
import com.seob.systemcore.error.ErrorCode;

public class AlreadyExistsEmailException extends ServiceException {
    public static final ServiceException EXCEPTION = new AlreadyExistsEmailException();

    private AlreadyExistsEmailException() {
        super(ErrorCode.ALREADY_EXISTS_EMAIL);
    }
}
