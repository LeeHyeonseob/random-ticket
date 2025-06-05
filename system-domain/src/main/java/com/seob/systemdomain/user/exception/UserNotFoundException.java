package com.seob.systemdomain.user.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;

public class UserNotFoundException extends DomainException {
    public static final UserNotFoundException EXCEPTION = new UserNotFoundException();

    private UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}
