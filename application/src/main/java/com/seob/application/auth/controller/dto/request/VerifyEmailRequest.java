package com.seob.application.auth.controller.dto.request;

public record VerifyEmailRequest(
        String email,
        String code
) {
}
