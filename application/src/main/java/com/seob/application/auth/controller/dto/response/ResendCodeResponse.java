package com.seob.application.auth.controller.dto.response;

import com.seob.application.auth.service.dto.ResendCodeServiceResponse;

public record ResendCodeResponse(String message) {
    public static ResendCodeResponse of(ResendCodeServiceResponse serviceResponse) {
        return new ResendCodeResponse(serviceResponse.message());
    }
}
