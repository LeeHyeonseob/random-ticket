package com.seob.application.auth.exception;

import com.seob.application.exception.ServiceException;
import com.seob.systemcore.error.ErrorCode;

public class UserNotActiveException extends ServiceException {
    public static final ServiceException EXCEPTION = new UserNotActiveException();

    private UserNotActiveException() {
        super(ErrorCode.USER_NOT_ACTIVE);
    }
}
