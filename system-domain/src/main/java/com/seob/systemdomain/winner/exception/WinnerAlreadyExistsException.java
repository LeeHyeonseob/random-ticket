package com.seob.systemdomain.winner.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;

/**
 * 이미 당첨자가 존재할 때 발생하는 예외
 */
public class WinnerAlreadyExistsException extends DomainException {
    public static final WinnerAlreadyExistsException EXCEPTION = new WinnerAlreadyExistsException();
    
    private WinnerAlreadyExistsException() {
        super(ErrorCode.ALREADY_WINNER_EXISTS);
    }
}
