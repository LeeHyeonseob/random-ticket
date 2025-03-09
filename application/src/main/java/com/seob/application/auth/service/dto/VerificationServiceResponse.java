package com.seob.application.auth.service.dto;

public record VerificationServiceResponse(
        String message
) {
    public static VerificationServiceResponse of() {
        return new VerificationServiceResponse("이메일 인증이 완료되었습니다.");
    }
}
