package com.seob.application.auth.controller.dto.request;

public record RegisterRequest(
        String email,
        String password,
        String nickname
) {
}
