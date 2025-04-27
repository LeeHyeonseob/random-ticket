package com.seob.systemdomain.ticket.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;

/**
 * 티켓 관련 예외의 기본 클래스
 */
public abstract class TicketException extends DomainException {
    protected TicketException(ErrorCode errorCode) {
        super(errorCode);
    }
}
