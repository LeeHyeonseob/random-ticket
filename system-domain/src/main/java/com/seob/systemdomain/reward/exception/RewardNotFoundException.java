package com.seob.systemdomain.reward.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;

/**
 * 보상을 찾을 수 없을 때 발생하는 예외
 */
public class RewardNotFoundException extends DomainException {
    public static final RewardNotFoundException EXCEPTION = new RewardNotFoundException();
    
    private RewardNotFoundException() {
        super(ErrorCode.REWARD_NOT_FOUND);
    }
}
