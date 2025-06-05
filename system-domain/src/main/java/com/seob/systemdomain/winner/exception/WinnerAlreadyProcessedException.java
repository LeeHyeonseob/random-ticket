package com.seob.systemdomain.winner.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;

public class WinnerAlreadyProcessedException extends DomainException {
    public static final WinnerAlreadyProcessedException EXCEPTION = new WinnerAlreadyProcessedException();
    
    private WinnerAlreadyProcessedException() {
        super(ErrorCode.WINNER_ALREADY_PROCESSED);
    }
}
