package com.seob.systemdomain.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemcore.error.exception.BaseException;

public class DomainException extends BaseException {
    public DomainException(ErrorCode errorCode) {
        super(errorCode);
    }
}
