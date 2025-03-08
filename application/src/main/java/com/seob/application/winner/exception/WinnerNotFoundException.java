package com.seob.application.winner.exception;

import com.seob.application.exception.ServiceException;
import com.seob.systemcore.error.ErrorCode;

public class WinnerNotFoundException extends ServiceException {
    public static ServiceException EXCEPTION = new WinnerNotFoundException();

    private WinnerNotFoundException() {
        super(ErrorCode.WINNER_NOT_FOUND);
    }
}
