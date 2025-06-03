package com.seob.systemdomain.reward.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;


public class RewardNotFoundException extends DomainException {
    public static final RewardNotFoundException EXCEPTION = new RewardNotFoundException();
    
    private RewardNotFoundException() {
        super(ErrorCode.REWARD_NOT_FOUND);
    }
}
