package com.seob.application.auth.controller.dto.response;

import com.seob.application.auth.service.dto.AuthServiceResponse;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Long accessTokenValidity,
        String role
) {
    public static AuthResponse of(AuthServiceResponse serviceResponse) {
        return new AuthResponse(
            serviceResponse.accessToken(), 
            serviceResponse.refreshToken(), 
            serviceResponse.accessTokenValidity(),
            serviceResponse.role()
        );
    }
}
