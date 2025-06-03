package com.seob.systemdomain.reward.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;


public class NoRewardInEventException extends DomainException {
    public static final NoRewardInEventException EXCEPTION = new NoRewardInEventException();
    
    private NoRewardInEventException() {
        super(ErrorCode.NO_REWARD_IN_EVENT);
    }
}
