package com.seob.application.winner.exception;

import com.seob.application.exception.ServiceException;
import com.seob.systemcore.error.ErrorCode;

public class NoRewardInEventException extends ServiceException {
    public static ServiceException EXCEPTION = new NoRewardInEventException();

    private NoRewardInEventException() {
        super(ErrorCode.NO_REWARD_IN_EVENT);
    }
}
