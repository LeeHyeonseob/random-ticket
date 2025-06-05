package com.seob.systeminfra.winner.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systeminfra.exception.InfraException;

public class WinnerDataAccessException extends InfraException {
    public static final WinnerDataAccessException EXCEPTION = new WinnerDataAccessException(ErrorCode.INTERNAL_SERVER_ERROR);
    
    private WinnerDataAccessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
