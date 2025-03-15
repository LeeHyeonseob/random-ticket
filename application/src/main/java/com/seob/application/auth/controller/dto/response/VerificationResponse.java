package com.seob.application.auth.controller.dto.response;

import com.seob.application.auth.service.dto.VerificationServiceResponse;

public record VerificationResponse(String message) {
    public static VerificationResponse of(VerificationServiceResponse serviceResponse) {
        return new VerificationResponse(serviceResponse.message());
    }
}
