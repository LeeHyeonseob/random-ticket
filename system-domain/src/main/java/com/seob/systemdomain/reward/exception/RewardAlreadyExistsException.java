package com.seob.systemdomain.reward.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;


public class RewardAlreadyExistsException extends DomainException {
    public static final RewardAlreadyExistsException EXCEPTION = new RewardAlreadyExistsException();
    
    private RewardAlreadyExistsException() {
        super(ErrorCode.REWARD_ALREADY_EXISTS);
    }
}
