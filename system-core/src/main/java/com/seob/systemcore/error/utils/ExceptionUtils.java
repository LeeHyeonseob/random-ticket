package com.seob.systemcore.error.utils;


import com.seob.systemcore.error.ErrorCode;
import com.seob.systemcore.error.exception.BaseException;
import com.seob.systemcore.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
public class ExceptionUtils {

    // 예외 변환 기능 (존재하는 경우만)
    public static RuntimeException convertToRuntimeException(Exception ex) {
        if (ex instanceof BaseException) {
            return (BaseException) ex;
        }
        return new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    // 예외로부터 ErrorCode 추출 기능
    public static ErrorCode getErrorCode(Exception ex) {
        if (ex instanceof BaseException) {
            return ((BaseException) ex).getErrorCode();
        }
        return ErrorCode.INTERNAL_SERVER_ERROR;
    }

    // 로깅 유틸리티
    public static void logException(Exception ex) {
        if (ex instanceof BaseException) {
            ErrorCode errorCode = ((BaseException) ex).getErrorCode();
            log.error("Business exception occurred: {} - {}", errorCode.getCode(), errorCode.getMessage(), ex);
        } else {
            log.error("Unexpected exception occurred", ex);
        }
    }
}