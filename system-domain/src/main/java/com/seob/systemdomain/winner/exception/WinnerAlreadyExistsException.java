package com.seob.systemdomain.winner.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;


public class WinnerAlreadyExistsException extends DomainException {
    public static final WinnerAlreadyExistsException EXCEPTION = new WinnerAlreadyExistsException();
    
    private WinnerAlreadyExistsException() {
        super(ErrorCode.ALREADY_WINNER_EXISTS);
    }
}
