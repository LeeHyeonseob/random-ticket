package com.seob.systeminfra.exception;

import com.seob.systemcore.error.ErrorCode;

public class DatabaseException extends InfraException {
    public static final DatabaseException CONNECTION_FAILED = new DatabaseException(ErrorCode.INTERNAL_SERVER_ERROR);
    
    private DatabaseException(ErrorCode errorCode) {
        super(errorCode);
    }
}
