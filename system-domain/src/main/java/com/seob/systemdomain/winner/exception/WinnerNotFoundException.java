package com.seob.systemdomain.winner.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;


public class WinnerNotFoundException extends DomainException {
    public static final WinnerNotFoundException EXCEPTION = new WinnerNotFoundException();
    
    private WinnerNotFoundException() {
        super(ErrorCode.WINNER_NOT_FOUND);
    }
}
