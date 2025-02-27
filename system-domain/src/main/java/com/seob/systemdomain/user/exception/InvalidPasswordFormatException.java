package com.seob.systemdomain.user.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;

public class InvalidPasswordFormatException extends DomainException {
    public static final DomainException EXCEPTION = new InvalidPasswordFormatException();

    private InvalidPasswordFormatException() {
        super(ErrorCode.INVALID_PASSWORD_FORMAT);
    }
}
