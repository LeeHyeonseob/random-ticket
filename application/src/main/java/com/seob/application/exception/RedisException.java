package com.seob.application.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemcore.error.exception.BaseException;

public class RedisException extends BaseException {
    public RedisException(ErrorCode errorCode) { super(errorCode); }
}
