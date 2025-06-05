package com.seob.systeminfra.user.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systeminfra.exception.InfraException;

public class UserDataAccessException extends InfraException {
    public static final UserDataAccessException NOT_FOUND = new UserDataAccessException(ErrorCode.INTERNAL_SERVER_ERROR);
    
    private UserDataAccessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
