package com.seob.application.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemcore.error.exception.BaseException;

public class AuthException extends BaseException {
    public AuthException(ErrorCode errorCode) {
        super(errorCode);}
}
