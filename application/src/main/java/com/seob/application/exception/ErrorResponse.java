package com.seob.application.exception;


import com.seob.systemcore.error.ErrorCode;
import lombok.Getter;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ErrorResponse {
    // getter 메서드
    private String timestamp;
    private String code;
    private String message;
    private List<FieldError> errors;

    private ErrorResponse(String code, String message) {
        this.timestamp = LocalDateTime.now().toString();
        this.code = code;
        this.message = message;
    }

    private ErrorResponse(String code, String message, List<FieldError> errors) {
        this.timestamp = LocalDateTime.now().toString();
        this.code = code;
        this.message = message;
        this.errors = errors;
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
    }

    public static ErrorResponse of(ErrorCode errorCode, BindingResult bindingResult) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), FieldError.of(bindingResult));
    }

    // 필드 에러 정보를 담는 중첩 클래스
    @Getter
    public static class FieldError {
        private String field;
        private String value;
        private String reason;

        private FieldError(String field, String value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static List<FieldError> of(BindingResult bindingResult) {
            List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()
                    ))
                    .collect(Collectors.toList());
        }
    }

}