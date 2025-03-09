package com.seob.application.auth.service.dto;

public record AuthServiceResponse(
        String accessToken,
        String refreshToken,
        Long accessTokenValidity
) {
    public static AuthServiceResponse of(String accessToken, String refreshToken, Long accessTokenValidity) {
        return new AuthServiceResponse(accessToken, refreshToken, accessTokenValidity);
    }
}
