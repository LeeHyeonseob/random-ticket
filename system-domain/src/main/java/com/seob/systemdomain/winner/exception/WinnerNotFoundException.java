package com.seob.systemdomain.winner.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;

/**
 * 당첨자를 찾을 수 없을 때 발생하는 예외
 */
public class WinnerNotFoundException extends DomainException {
    public static final WinnerNotFoundException EXCEPTION = new WinnerNotFoundException();
    
    private WinnerNotFoundException() {
        super(ErrorCode.WINNER_NOT_FOUND);
    }
}
