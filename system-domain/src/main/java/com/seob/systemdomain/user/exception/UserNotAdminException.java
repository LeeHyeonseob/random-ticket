package com.seob.systemdomain.user.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;

public class UserNotAdminException extends DomainException {
    public static final DomainException EXCEPTION = new UserNotAdminException();

    private UserNotAdminException() {
        super(ErrorCode.USER_NOT_ADMIN);
    }
}
