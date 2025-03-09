package com.seob.application.auth.controller.dto.request;

public record LoginRequest(
        String email,
        String password
) {
}
