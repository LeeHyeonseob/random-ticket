package com.seob.systemdomain.user.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;

public class UserNotActiveException extends DomainException {
    public static final UserNotActiveException EXCEPTION = new UserNotActiveException();

    private UserNotActiveException() {
        super(ErrorCode.USER_NOT_ACTIVE);
    }
}
