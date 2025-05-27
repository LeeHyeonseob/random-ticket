package com.seob.systemcore.error.exception;

import com.seob.systemcore.error.ErrorCode;

public class RewardException extends BaseException {
    public static final RewardException INVALID_REWARD_NAME = new RewardException(ErrorCode.INVALID_REWARD_NAME);
    public static final RewardException INVALID_REWARD_URL = new RewardException(ErrorCode.INVALID_REWARD_URL);
    public static final RewardException INVALID_EVENT_ID = new RewardException(ErrorCode.INVALID_EVENT_ID);
    public static final RewardException EVENT_NOT_FOUND_FOR_REWARD = new RewardException(ErrorCode.EVENT_NOT_FOUND_FOR_REWARD);
    public static final RewardException REWARD_ALREADY_EXISTS = new RewardException(ErrorCode.REWARD_ALREADY_EXISTS);

    private RewardException(ErrorCode errorCode) {
        super(errorCode);
    }
}
