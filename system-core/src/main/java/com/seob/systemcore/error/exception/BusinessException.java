package com.seob.systemcore.error.exception;

import com.seob.systemcore.error.ErrorCode;

public class BusinessException extends BaseException {
    public BusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
