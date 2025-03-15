package com.seob.application.winner.exception;

import com.seob.application.exception.ServiceException;
import com.seob.systemcore.error.ErrorCode;

public class AlreadyWinnerExistsException extends ServiceException {
    public static ServiceException EXCEPTION = new AlreadyWinnerExistsException();

    private AlreadyWinnerExistsException() {
        super(ErrorCode.ALREADY_WINNER_EXISTS);
    }
}
