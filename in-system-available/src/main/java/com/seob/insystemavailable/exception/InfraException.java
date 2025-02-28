package com.seob.insystemavailable.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemcore.error.exception.BaseException;

public class InfraException extends BaseException {
    public InfraException(ErrorCode errorCode) {
        super(errorCode);
    }
}
