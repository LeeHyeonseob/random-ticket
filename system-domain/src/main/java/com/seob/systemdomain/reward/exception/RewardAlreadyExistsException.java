package com.seob.systemdomain.reward.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;

/**
 * 이미 보상이 존재할 때 발생하는 예외
 */
public class RewardAlreadyExistsException extends DomainException {
    public static final RewardAlreadyExistsException EXCEPTION = new RewardAlreadyExistsException();
    
    private RewardAlreadyExistsException() {
        super(ErrorCode.REWARD_ALREADY_EXISTS);
    }
}
