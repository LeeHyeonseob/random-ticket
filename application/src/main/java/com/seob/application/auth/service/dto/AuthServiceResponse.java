package com.seob.application.auth.service.dto;

public record AuthServiceResponse(
        String accessToken,
        String refreshToken,
        Long accessTokenValidity,
        String role
) {
    public static AuthServiceResponse of(String accessToken, String refreshToken, Long accessTokenValidity, String role) {
        return new AuthServiceResponse(accessToken, refreshToken, accessTokenValidity, role);
    }
}
