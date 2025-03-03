package com.seob.application.ticket.exception;

import com.seob.application.exception.RedisException;
import com.seob.systemcore.error.ErrorCode;

public class PublishFailureException extends RedisException {

    public static final RedisException Exception = new PublishFailureException();

    private PublishFailureException() {
        super(ErrorCode.REDIS_PUBLISH_ERROR);
    }


}
