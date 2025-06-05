package com.seob.systeminfra.reward.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systeminfra.exception.InfraException;

/**
 * 인프라 계층 전용 예외
 * 보상 데이터 액세스 과정에서 발생하는 문제를 나타냄
 */
public class RewardDataAccessException extends InfraException {
    public static final RewardDataAccessException NOT_FOUND = new RewardDataAccessException(ErrorCode.INTERNAL_SERVER_ERROR);
    public static final RewardDataAccessException EVENT_NOT_FOUND = new RewardDataAccessException(ErrorCode.INTERNAL_SERVER_ERROR);
    public static final RewardDataAccessException REWARD_ALREADY_EXISTS = new RewardDataAccessException(ErrorCode.INTERNAL_SERVER_ERROR);
    
    private RewardDataAccessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
