package com.seob.application.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemcore.error.exception.BaseException;

public class ServiceException extends BaseException {
    public ServiceException(ErrorCode errorCode) {
        super(errorCode);
    }
}
