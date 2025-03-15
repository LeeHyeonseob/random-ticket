package com.seob.systemdomain.user.exception;

import com.seob.systemcore.error.ErrorCode;
import com.seob.systemdomain.exception.DomainException;

public class InvalidEmailFormatException extends DomainException {
    // 단일 instance를 static final로 선언 (싱글턴 패턴처럼 재사용)
    public static final DomainException EXCEPTION = new InvalidEmailFormatException();

    private InvalidEmailFormatException() {
        // DomainException(ErrorCode) 생성자 호출
        super(ErrorCode.INVALID_EMAIL_FORMAT);
    }
}
