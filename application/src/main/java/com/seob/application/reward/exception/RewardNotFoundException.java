package com.seob.application.reward.exception;

import com.seob.application.exception.ServiceException;
import com.seob.systemcore.error.ErrorCode;

public class RewardNotFoundException extends ServiceException {
    public static final ServiceException EXCEPTION = new RewardNotFoundException();

    private RewardNotFoundException() {
        super(ErrorCode.REWARD_NOT_FOUND);
    }
}
