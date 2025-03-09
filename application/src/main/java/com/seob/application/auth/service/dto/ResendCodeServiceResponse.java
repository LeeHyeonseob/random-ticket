package com.seob.application.auth.service.dto;

public record ResendCodeServiceResponse(String message) {
    public static ResendCodeServiceResponse of() {
        return new ResendCodeServiceResponse("인증 코드가 재전송되었습니다.");
    }
}
