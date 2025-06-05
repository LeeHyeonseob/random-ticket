package com.seob.systeminfra.exception;

import com.seob.systemcore.error.ErrorCode;

public class UserNotFoundException extends InfraException {
    public static final InfraException EXCEPTION = new UserNotFoundException();

    private UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}
