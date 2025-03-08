package com.seob.systeminfra.reward.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systeminfra.exception.InfraException;

public class RewardNotFoundException extends InfraException {
    public static final InfraException EXCEPTION = new RewardNotFoundException();

    private RewardNotFoundException() {
        super(ErrorCode.REWARD_NOT_FOUND);
    }
}
