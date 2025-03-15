package com.seob.systeminfra.ticket.exception;


import com.seob.systemcore.error.ErrorCode;
import com.seob.systeminfra.exception.RedisException;

public class PublishFailureException extends RedisException {

    public static final RedisException Exception = new PublishFailureException();

    private PublishFailureException() {
        super(ErrorCode.REDIS_PUBLISH_ERROR);
    }


}
