package com.seob.systemdomain.reward.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;

/**
 * 이벤트에 보상이 없을 때 발생하는 예외
 */
public class NoRewardInEventException extends DomainException {
    public static final NoRewardInEventException EXCEPTION = new NoRewardInEventException();
    
    private NoRewardInEventException() {
        super(ErrorCode.NO_REWARD_IN_EVENT);
    }
}
