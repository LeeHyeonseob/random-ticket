package com.seob.systeminfra.exception;

import com.seob.systemcore.error.ErrorCode;

public class EmailSendException extends InfraException {
    public static final EmailSendException SEND_FAILED = new EmailSendException(ErrorCode.INTERNAL_SERVER_ERROR);
    
    private EmailSendException(ErrorCode errorCode) {
        super(errorCode);
    }
}
