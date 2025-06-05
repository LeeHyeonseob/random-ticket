package com.seob.systemdomain.user.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;

public class AlreadyExistsEmailException extends DomainException {
    public static final DomainException EXCEPTION = new AlreadyExistsEmailException();

    private AlreadyExistsEmailException() {
        super(ErrorCode.ALREADY_EXISTS_EMAIL);
    }
}
