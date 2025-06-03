package com.seob.systemdomain.entry.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;


public class EntryNotFoundException extends DomainException {
    public static final EntryNotFoundException EXCEPTION = new EntryNotFoundException();
    
    private EntryNotFoundException() {
        super(ErrorCode.ENTRY_NOT_FOUND);
    }
}
