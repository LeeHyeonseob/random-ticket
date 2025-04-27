package com.seob.systemdomain.entry.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;

/**
 * 참가 정보를 찾을 수 없을 때 발생하는 예외
 */
public class EntryNotFoundException extends DomainException {
    public static final EntryNotFoundException EXCEPTION = new EntryNotFoundException();
    
    private EntryNotFoundException() {
        super(ErrorCode.ENTRY_NOT_FOUND);
    }
}
