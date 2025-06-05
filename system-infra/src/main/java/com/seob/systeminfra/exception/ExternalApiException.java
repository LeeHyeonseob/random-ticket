package com.seob.systeminfra.exception;

import com.seob.systemcore.error.ErrorCode;

public class ExternalApiException extends InfraException {
    public static final ExternalApiException API_CALL_FAILED = new ExternalApiException(ErrorCode.INTERNAL_SERVER_ERROR);
    
    private ExternalApiException(ErrorCode errorCode) {
        super(errorCode);
    }
}
