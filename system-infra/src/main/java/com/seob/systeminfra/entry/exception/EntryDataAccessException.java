package com.seob.systeminfra.entry.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systeminfra.exception.InfraException;

/**
 * 인프라 계층 전용 예외
 * 참여 데이터 액세스 과정에서 발생하는 문제를 나타냄
 */
public class EntryDataAccessException extends InfraException {
    public static final EntryDataAccessException EVENT_NOT_FOUND = new EntryDataAccessException(ErrorCode.INTERNAL_SERVER_ERROR);
    public static final EntryDataAccessException TICKET_NOT_FOUND = new EntryDataAccessException(ErrorCode.INTERNAL_SERVER_ERROR);
    
    private EntryDataAccessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
